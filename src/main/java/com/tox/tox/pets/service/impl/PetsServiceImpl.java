package com.tox.tox.pets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tox.tox.pets.model.*;
import com.tox.tox.pets.mapper.PetsMapper;
import com.tox.tox.pets.model.dto.ActivityLogDTO;
import com.tox.tox.pets.model.dto.DashboardSummaryDTO;
import com.tox.tox.pets.model.dto.HealthEventsDTO;
import com.tox.tox.pets.model.dto.HealthReportDTO;
import com.tox.tox.pets.model.dto.NotificationItemDTO;
import com.tox.tox.pets.model.dto.NotificationPrefsDTO;
import com.tox.tox.pets.model.dto.PetDetailDTO;
import com.tox.tox.pets.model.dto.PetPageDTO;
import com.tox.tox.pets.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.tox.tox.pets.model.dto.PetLeaderboardDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.OffsetDateTime;
import java.util.Collections;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Service
public class PetsServiceImpl extends ServiceImpl<PetsMapper, Pets> implements IPetsService {

    private static final Logger log = LoggerFactory.getLogger(PetsServiceImpl.class);
    
    private LikingService likingService;

    @Autowired
    private IDictItemsService dictItemsService;

    @Autowired
    private IWeightLogService weightLogService;

    @Autowired
    private IHealthEventsService healthEventsService;

    @Autowired
    private IFeedingRecordService feedingRecordService;

    @Autowired
    private IBathingRecordService bathingRecordService;

    @Autowired
    private IMedicationRecordService medicationRecordService;

    @Autowired
    private IPetGalleryService petGalleryService;

    @Autowired
    private ObjectMapper objectMapper; // 新增

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    public void setLikingService(LikingService likingService) {
        this.likingService = likingService;
    }

    /**
     * (❗ 核心实现)
     */
    @Override
//    @Cacheable(value = "pets_page", key = "#pageNum + '-' + #pageSize")
    public IPage<PetPageDTO> findPetsWithLikes(int pageNum, int pageSize, String name, Long speciesId, String gender, Integer ageMinMonths, Integer ageMaxMonths, String sort) {

        // 1. (DB) 创建 MP 分页对象
        IPage<Pets> petPageConfig = new Page<>(pageNum, pageSize);

        // 2. (DB) 构建查询条件，支持名称/备注搜索、物种筛选、性别筛选、年龄范围筛选
        QueryWrapper<Pets> queryWrapper = new QueryWrapper<>();
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.and(w -> w.like("name", name.trim()).or().like("notes", name.trim()));
        }
        if (speciesId != null) {
            queryWrapper.eq("species_id", speciesId);
        }
        if (gender != null && !gender.isEmpty()) {
            queryWrapper.eq("gender", gender);
        }
        // 年龄范围筛选：通过 birthday 列计算，ageMaxMonths 对应最小生日（最年轻），ageMinMonths 对应最大生日（最年长）
        if (ageMaxMonths != null) {
            LocalDate minBirthday = LocalDate.now().minusMonths(ageMaxMonths);
            queryWrapper.ge("birthday", minBirthday);
        }
        if (ageMinMonths != null) {
            LocalDate maxBirthday = LocalDate.now().minusMonths(ageMinMonths);
            queryWrapper.le("birthday", maxBirthday);
        }

        // 2.5 排序：DB 可排序的字段直接在查询时排序，likes 需要 Redis 后排序
        boolean sortByLikes = false;
        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "name_asc":
                    queryWrapper.orderByAsc("name");
                    break;
                case "age_asc":
                    queryWrapper.orderByAsc("birthday");
                    break;
                case "age_desc":
                    queryWrapper.orderByDesc("birthday");
                    break;
                case "newest":
                    queryWrapper.orderByDesc("id");
                    break;
                case "likes_desc":
                    sortByLikes = true;
                    queryWrapper.orderByDesc("id");
                    break;
                default:
                    queryWrapper.orderByAsc("id");
                    break;
            }
        } else {
            queryWrapper.orderByAsc("id");
        }

        // 3. (DB) 执行 MP 分页查询
        IPage<Pets> petPage = this.page(petPageConfig, queryWrapper);

        List<Pets> pets = petPage.getRecords();

        // (如果为空, 提前返回一个空的 DTO 分页)
        if (pets.isEmpty()) {
            return new Page<>(pageNum, pageSize, petPage.getTotal());
        }

        // 3. (Redis) 准备批量查询
        List<Long> petIds = pets.stream()
                .map(Pets::getId)
                .collect(Collectors.toList());

        // 4. (Redis) (❗) 一次性 MGET (LikingService 保持不变)
        Map<Long, Long> likeCountsMap = likingService.getPetLikeCounts(petIds);

        // 5. (Java) 组装 DTO 列表
        List<PetPageDTO> dtos = pets.stream().map(pet -> {
            PetPageDTO dto = new PetPageDTO(pet);
            long likeCount = likeCountsMap.getOrDefault(pet.getId(), 0L);
            dto.setLikeCount(likeCount);
            return dto;
        }).collect(Collectors.toList());

        // 5.5 按点赞数排序（当前页内，Redis 数据在 Java 层排序）
        if (sortByLikes) {
            dtos.sort((a, b) -> Long.compare(b.getLikeCount(), a.getLikeCount()));
        }

        // 6. (Java) 创建 DTO 分页结果
        IPage<PetPageDTO> dtoPage = new Page<>(petPage.getCurrent(), petPage.getSize(), petPage.getTotal());
        dtoPage.setRecords(dtos);

        log.info("Pagination result - Total: {}, Pages: {}", dtoPage.getTotal(), dtoPage.getPages());

        return dtoPage;
    }

    @Override
    public List<PetLeaderboardDTO> getLeaderboard(int topN) {
        // 1. (Redis) 从 LikingService 获取排行榜 (成员是 JSON, 分数是点赞数)
        List<ZSetOperations.TypedTuple<String>> leaderboardFromRedis = likingService.getLeaderboard(topN);

        if (leaderboardFromRedis.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. (Java) 解析 JSON 并组装成 DTO, 不再查询数据库
        return IntStream.range(0, leaderboardFromRedis.size())
                .mapToObj(i -> {
                    ZSetOperations.TypedTuple<String> tuple = leaderboardFromRedis.get(i);
                    String memberJson = tuple.getValue();
                    Double likeCount = tuple.getScore();

                    if (memberJson == null || likeCount == null) {
                        return null;
                    }

                    try {
                        // 将 JSON 字符串反序列化为 Map
                        Map<String, Object> member = objectMapper.readValue(memberJson, Map.class);

                        return new PetLeaderboardDTO(
                                i + 1, // Rank
                                ((Number) member.get("petId")).longValue(),
                                (String) member.get("name"),
                                (String) member.get("profileImageUrl"),
                                likeCount.longValue()
                        );
                    } catch (Exception e) {
                        log.error("反序列化排行榜成员失败: {}", memberJson, e);
                        return null; // 忽略解析失败的数据
                    }
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "pets_detail_by_id", key = "#id")
    public PetDetailDTO getPetDetailById(Long id) {
        // 1. 查询宠物基本信息
        Pets pet = this.getById(id);
        if (pet == null) {
            return null;
        }

        // 2. 创建详细信息DTO并设置基本信息
        PetDetailDTO detailDTO = new PetDetailDTO();
        detailDTO.setId(pet.getId());
        detailDTO.setSpeciesId(pet.getSpeciesId());
        detailDTO.setBreedId(pet.getBreedId());
        detailDTO.setName(pet.getName());
        detailDTO.setGender(pet.getGender());
        detailDTO.setNotes(pet.getNotes());
        detailDTO.setBirthday(pet.getBirthday());
        detailDTO.setCreatedAt(pet.getCreatedAt());
        detailDTO.setAvatarUrl(pet.getProfileImageUrl());
        detailDTO.setAvatarId(pet.getProfileImagePublicId());

        // 3. 查询物种中文标签
        if (pet.getSpeciesId() != null) {
            DictItems speciesDict = dictItemsService.getById(pet.getSpeciesId());
            if (speciesDict != null) {
                detailDTO.setSpeciesLabel(speciesDict.getItemLabel());
            }
        }

        // 4. 查询品种中文标签
        if (pet.getBreedId() != null) {
            DictItems breedDict = dictItemsService.getById(pet.getBreedId());
            if (breedDict != null) {
                detailDTO.setBreedLabel(breedDict.getItemLabel());
            }
        }

        // 5. 查询体重记录
        QueryWrapper<WeightLog> weightQuery = new QueryWrapper<>();
        weightQuery.eq("pet_id", id);
        weightQuery.orderByDesc("log_date");
        List<WeightLog> weightLogs = weightLogService.list(weightQuery);
        detailDTO.setWeightLogs(weightLogs);

        // 6. 查询健康事件并添加中文标签
        QueryWrapper<HealthEvents> healthQuery = new QueryWrapper<>();
        healthQuery.eq("pet_id", id);
        healthQuery.orderByDesc("event_date");
        List<HealthEvents> healthEvents = healthEventsService.list(healthQuery);

        // 转换为带中文标签的DTO列表
        List<HealthEventsDTO> healthEventsDTOs = new ArrayList<>();
        for (HealthEvents event : healthEvents) {
            HealthEventsDTO eventDTO = new HealthEventsDTO();

            // 设置基本属性
            eventDTO.setId(event.getId());
            eventDTO.setPetId(event.getPetId());
            eventDTO.setEventTypeId(event.getEventTypeId());
            eventDTO.setEventDate(event.getEventDate());
            eventDTO.setNextDueDate(event.getNextDueDate());
            eventDTO.setNotes(event.getNotes());
            eventDTO.setStatus(event.getStatus());
            eventDTO.setCreatedAt(event.getCreatedAt());

            // 查询并设置事件类型中文标签
            if (event.getEventTypeId() != null) {
                DictItems eventTypeDict = dictItemsService.getById(event.getEventTypeId());
                if (eventTypeDict != null) {
                    eventDTO.setEventTypeLabel(eventTypeDict.getItemLabel());
                }
            }

            healthEventsDTOs.add(eventDTO);
        }

        detailDTO.setHealthEvents(healthEventsDTOs);

        // 7. 查询喂养记录
        QueryWrapper<FeedingRecord> feedingQuery = new QueryWrapper<>();
        feedingQuery.eq("pet_id", id);
        feedingQuery.orderByDesc("feed_time");
        List<FeedingRecord> feedingRecords = feedingRecordService.list(feedingQuery);
        detailDTO.setFeedingRecords(feedingRecords);

        // 8. 查询洗澡美容记录
        QueryWrapper<BathingRecord> bathingQuery = new QueryWrapper<>();
        bathingQuery.eq("pet_id", id);
        bathingQuery.orderByDesc("bath_time");
        List<BathingRecord> bathingRecords = bathingRecordService.list(bathingQuery);
        detailDTO.setBathingRecords(bathingRecords);

        // 9. 查询用药记录
        QueryWrapper<MedicationRecord> medicationQuery = new QueryWrapper<>();
        medicationQuery.eq("pet_id", id);
        medicationQuery.orderByDesc("start_date");
        List<MedicationRecord> medicationRecords = medicationRecordService.list(medicationQuery);
        detailDTO.setMedicationRecords(medicationRecords);

        // 10. 查询点赞数
        detailDTO.setLikeCount(likingService.getPetLikeCount(id));

        return detailDTO;
    }

    @Override
    public DashboardSummaryDTO getDashboardSummary() {
        DashboardSummaryDTO summary = new DashboardSummaryDTO();

        // 1. 总计数据
        summary.setTotalPets((int) this.count());

        QueryWrapper<PetGallery> galleryCountQuery = new QueryWrapper<>();
        summary.setTotalPhotos(petGalleryService.count(galleryCountQuery));

        QueryWrapper<HealthEvents> pendingQuery = new QueryWrapper<>();
        pendingQuery.and(w -> w.eq("status", 0).or().isNull("status"));
        summary.setPendingEvents(healthEventsService.count(pendingQuery));

        summary.setTotalWeightRecords(weightLogService.count(new QueryWrapper<>()));
        summary.setTotalHealthEvents(healthEventsService.count(new QueryWrapper<>()));
        summary.setTotalFeedings(feedingRecordService.count(new QueryWrapper<>()));
        summary.setTotalMedications(medicationRecordService.count(new QueryWrapper<>()));
        summary.setTotalBathingRecords(bathingRecordService.count(new QueryWrapper<>()));

        // 2. 批量加载最近活动数据
        List<DashboardSummaryDTO.ActivityItem> activities = new ArrayList<>();

        // 批量查询宠物名称
        List<Pets> allPets = this.list();
        Map<Long, String> petNameMap = allPets.stream()
                .collect(Collectors.toMap(Pets::getId, Pets::getName, (a, b) -> a));

        // 批量查询事件类型标签
        List<DictItems> allEventTypes = dictItemsService.list();
        Map<Long, String> eventTypeLabelMap = allEventTypes.stream()
                .collect(Collectors.toMap(DictItems::getId, DictItems::getItemLabel, (a, b) -> a));

        // 最近体重记录
        QueryWrapper<WeightLog> recentWeightQuery = new QueryWrapper<>();
        recentWeightQuery.orderByDesc("log_date");
        recentWeightQuery.last("LIMIT 5");
        List<WeightLog> recentWeights = weightLogService.list(recentWeightQuery);
        for (WeightLog w : recentWeights) {
            DashboardSummaryDTO.ActivityItem item = new DashboardSummaryDTO.ActivityItem();
            item.setId("w-" + w.getId());
            item.setType("weight");
            item.setDate(w.getLogDate() != null ? w.getLogDate().toString() : null);
            item.setPetName(petNameMap.getOrDefault(w.getPetId(), "未知宠物"));
            item.setTitle(w.getWeightKg() + " kg");
            item.setIcon("⚖️");
            activities.add(item);
        }

        // 最近健康事件
        QueryWrapper<HealthEvents> recentEventQuery = new QueryWrapper<>();
        recentEventQuery.orderByDesc("event_date");
        recentEventQuery.last("LIMIT 5");
        List<HealthEvents> recentEvents = healthEventsService.list(recentEventQuery);
        for (HealthEvents e : recentEvents) {
            DashboardSummaryDTO.ActivityItem item = new DashboardSummaryDTO.ActivityItem();
            item.setId("h-" + e.getId());
            item.setType("health");
            item.setDate(e.getEventDate() != null ? e.getEventDate().toString() : null);
            item.setPetName(petNameMap.getOrDefault(e.getPetId(), "未知宠物"));
            item.setTitle(eventTypeLabelMap.getOrDefault(e.getEventTypeId(), "健康事件"));
            item.setIcon("🩺");
            activities.add(item);
        }

        // 最近喂养记录
        QueryWrapper<FeedingRecord> recentFeedingQuery = new QueryWrapper<>();
        recentFeedingQuery.orderByDesc("feed_time");
        recentFeedingQuery.last("LIMIT 5");
        List<FeedingRecord> recentFeedings = feedingRecordService.list(recentFeedingQuery);
        for (FeedingRecord f : recentFeedings) {
            DashboardSummaryDTO.ActivityItem item = new DashboardSummaryDTO.ActivityItem();
            item.setId("f-" + f.getId());
            item.setType("feeding");
            item.setDate(f.getFeedTime() != null ? f.getFeedTime().toLocalDate().toString() : null);
            item.setPetName(petNameMap.getOrDefault(f.getPetId(), "未知宠物"));
            String amountStr = f.getAmountGrams() != null ? f.getAmountGrams() + "g" : "";
            String title = f.getFoodType() != null && !f.getFoodType().isEmpty()
                    ? f.getFoodType() + (amountStr.isEmpty() ? "" : " · " + amountStr)
                    : "喂食";
            item.setTitle(title);
            item.setIcon("🍽️");
            activities.add(item);
        }

        // 最近照片
        QueryWrapper<PetGallery> recentPhotoQuery = new QueryWrapper<>();
        recentPhotoQuery.orderByDesc("created_at");
        recentPhotoQuery.last("LIMIT 5");
        List<PetGallery> recentPhotos = petGalleryService.list(recentPhotoQuery);
        for (PetGallery p : recentPhotos) {
            DashboardSummaryDTO.ActivityItem item = new DashboardSummaryDTO.ActivityItem();
            item.setId("p-" + p.getId());
            item.setType("photo");
            item.setDate(p.getCreatedAt() != null ? p.getCreatedAt().toLocalDate().toString() : null);
            item.setPetName(petNameMap.getOrDefault(p.getPetId(), "未知宠物"));
            item.setTitle(p.getDescription() != null && !p.getDescription().isEmpty() ? p.getDescription() : "上传照片");
            item.setIcon("📷");
            activities.add(item);
        }

        // 最近用药记录
        QueryWrapper<MedicationRecord> recentMedQuery = new QueryWrapper<>();
        recentMedQuery.orderByDesc("start_date");
        recentMedQuery.last("LIMIT 5");
        List<MedicationRecord> recentMeds = medicationRecordService.list(recentMedQuery);
        for (MedicationRecord m : recentMeds) {
            DashboardSummaryDTO.ActivityItem item = new DashboardSummaryDTO.ActivityItem();
            item.setId("m-" + m.getId());
            item.setType("medication");
            item.setDate(m.getStartDate() != null ? m.getStartDate().toString() : null);
            item.setPetName(petNameMap.getOrDefault(m.getPetId(), "未知宠物"));
            String dosage = m.getDosage() != null && !m.getDosage().isEmpty() ? " · " + m.getDosage() : "";
            item.setTitle(m.getMedicationName() + dosage);
            item.setIcon("💊");
            activities.add(item);
        }

        // 最近洗澡美容记录
        QueryWrapper<BathingRecord> recentBathQuery = new QueryWrapper<>();
        recentBathQuery.orderByDesc("bath_time");
        recentBathQuery.last("LIMIT 5");
        List<BathingRecord> recentBaths = bathingRecordService.list(recentBathQuery);
        for (BathingRecord b : recentBaths) {
            DashboardSummaryDTO.ActivityItem item = new DashboardSummaryDTO.ActivityItem();
            item.setId("b-" + b.getId());
            item.setType("bathing");
            item.setDate(b.getBathTime() != null ? b.getBathTime().toLocalDate().toString() : null);
            item.setPetName(petNameMap.getOrDefault(b.getPetId(), "未知宠物"));
            item.setTitle(b.getServiceType() != null ? b.getServiceType() : "洗澡美容");
            item.setIcon("🛁");
            activities.add(item);
        }

        // 按日期降序排序，取最近 10 条
        activities.sort((a, b) -> {
            if (a.getDate() == null && b.getDate() == null) return 0;
            if (a.getDate() == null) return 1;
            if (b.getDate() == null) return -1;
            return b.getDate().compareTo(a.getDate());
        });
        if (activities.size() > 10) {
            activities = new ArrayList<>(activities.subList(0, 10));
        }

        summary.setRecentActivities(activities);

        // 3. 宠物速览数据
        List<DashboardSummaryDTO.PetOverviewItem> petOverviews = new ArrayList<>();
        if (!allPets.isEmpty()) {
            // 物种和品种名称映射
            Map<Long, String> dictLabelMap = allEventTypes.stream()
                    .collect(Collectors.toMap(DictItems::getId, DictItems::getItemLabel, (a, b) -> a));

            // 每只宠物的待处理事件数量
            QueryWrapper<HealthEvents> allPendingQuery = new QueryWrapper<>();
            allPendingQuery.and(w -> w.eq("status", 0).or().isNull("status"));
            allPendingQuery.select("pet_id");
            List<HealthEvents> allPending = healthEventsService.list(allPendingQuery);
            Map<Long, Long> petPendingCountMap = allPending.stream()
                    .collect(Collectors.groupingBy(HealthEvents::getPetId, Collectors.counting()));

            // 每只宠物的最新体重
            QueryWrapper<WeightLog> allWeightQuery = new QueryWrapper<>();
            allWeightQuery.orderByDesc("log_date");
            allWeightQuery.select("pet_id", "weight_kg", "log_date");
            List<WeightLog> allWeights = weightLogService.list(allWeightQuery);
            Map<Long, String> petLatestWeightMap = new java.util.HashMap<>();
            for (WeightLog w : allWeights) {
                petLatestWeightMap.putIfAbsent(w.getPetId(), w.getWeightKg() + " kg");
            }

            // 点赞数映射
            List<Long> petIdsForLikes = allPets.stream().map(Pets::getId).collect(Collectors.toList());
            Map<Long, Long> likeCountsMap = likingService.getPetLikeCounts(petIdsForLikes);

            for (Pets pet : allPets) {
                DashboardSummaryDTO.PetOverviewItem overview = new DashboardSummaryDTO.PetOverviewItem();
                overview.setId(pet.getId());
                overview.setName(pet.getName());
                overview.setGender(pet.getGender());
                overview.setBirthday(pet.getBirthday() != null ? pet.getBirthday().toString() : null);
                overview.setSpeciesName(dictLabelMap.getOrDefault(pet.getSpeciesId(), null));
                overview.setBreedName(dictLabelMap.getOrDefault(pet.getBreedId(), null));
                overview.setLatestWeight(petLatestWeightMap.get(pet.getId()));
                overview.setPendingEventsCount(petPendingCountMap.getOrDefault(pet.getId(), 0L));
                overview.setLikeCount(likeCountsMap.getOrDefault(pet.getId(), 0L));
                overview.setAvatarUrl(pet.getProfileImageUrl());
                petOverviews.add(overview);
            }
        }
        summary.setPetOverviews(petOverviews);

        // 4. 近期喂养统计（最近 30 天按食物类型分组）
        OffsetDateTime thirtyDaysAgo = OffsetDateTime.now().minusDays(30);
        QueryWrapper<FeedingRecord> feedingStatsQuery = new QueryWrapper<>();
        feedingStatsQuery.ge("feed_time", thirtyDaysAgo);
        feedingStatsQuery.select("food_type", "amount_grams");
        List<FeedingRecord> recentFeedingRecords = feedingRecordService.list(feedingStatsQuery);

        Map<String, List<FeedingRecord>> groupedByFoodType = recentFeedingRecords.stream()
                .filter(f -> f.getFoodType() != null && !f.getFoodType().isEmpty())
                .collect(Collectors.groupingBy(FeedingRecord::getFoodType));

        List<DashboardSummaryDTO.FeedingStatItem> feedingStats = new ArrayList<>();
        for (Map.Entry<String, List<FeedingRecord>> entry : groupedByFoodType.entrySet()) {
            DashboardSummaryDTO.FeedingStatItem stat = new DashboardSummaryDTO.FeedingStatItem();
            stat.setFoodType(entry.getKey());
            stat.setCount(entry.getValue().size());
            double avg = entry.getValue().stream()
                    .filter(f -> f.getAmountGrams() != null)
                    .mapToInt(FeedingRecord::getAmountGrams)
                    .average()
                    .orElse(0);
            stat.setAvgAmount(avg > 0 ? (int) Math.round(avg) : null);
            feedingStats.add(stat);
        }
        feedingStats.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));
        summary.setFeedingStats(feedingStats);

        return summary;
    }

    @Override
    public HealthReportDTO getHealthReport(Long petId, int months) {
        Pets pet = this.getById(petId);
        if (pet == null) {
            return null;
        }

        HealthReportDTO report = new HealthReportDTO();
        report.setPetId(petId);
        report.setPetName(pet.getName());
        report.setMonths(months);

        if (pet.getSpeciesId() != null) {
            DictItems speciesDict = dictItemsService.getById(pet.getSpeciesId());
            if (speciesDict != null) report.setSpeciesLabel(speciesDict.getItemLabel());
        }
        if (pet.getBreedId() != null) {
            DictItems breedDict = dictItemsService.getById(pet.getBreedId());
            if (breedDict != null) report.setBreedLabel(breedDict.getItemLabel());
        }

        LocalDate startDate = LocalDate.now().minusMonths(months);

        // Weight logs
        QueryWrapper<WeightLog> weightQuery = new QueryWrapper<>();
        weightQuery.eq("pet_id", petId);
        weightQuery.ge("log_date", startDate);
        weightQuery.orderByAsc("log_date");
        List<WeightLog> weightLogs = weightLogService.list(weightQuery);

        // Health events
        QueryWrapper<HealthEvents> eventQuery = new QueryWrapper<>();
        eventQuery.eq("pet_id", petId);
        eventQuery.ge("event_date", startDate);
        List<HealthEvents> healthEvents = healthEventsService.list(eventQuery);

        // Feeding records
        QueryWrapper<FeedingRecord> feedingQuery = new QueryWrapper<>();
        feedingQuery.eq("pet_id", petId);
        feedingQuery.ge("feed_time", startDate.atStartOfDay().atOffset(java.time.ZoneOffset.UTC));
        List<FeedingRecord> feedingRecords = feedingRecordService.list(feedingQuery);

        // Bathing records
        QueryWrapper<BathingRecord> bathingQuery = new QueryWrapper<>();
        bathingQuery.eq("pet_id", petId);
        bathingQuery.ge("bath_time", startDate.atStartOfDay().atOffset(java.time.ZoneOffset.UTC));
        List<BathingRecord> bathingRecords = bathingRecordService.list(bathingQuery);

        // Medication records
        QueryWrapper<MedicationRecord> medicationQuery = new QueryWrapper<>();
        medicationQuery.eq("pet_id", petId);
        medicationQuery.ge("start_date", startDate);
        List<MedicationRecord> medicationRecords = medicationRecordService.list(medicationQuery);

        // Aggregate by month
        java.util.LinkedHashMap<String, HealthReportDTO.MonthlyData> monthMap = new java.util.LinkedHashMap<>();
        LocalDate current = startDate.withDayOfMonth(1);
        LocalDate end = LocalDate.now().withDayOfMonth(1).plusMonths(1);
        while (current.isBefore(end)) {
            String key = current.getYear() + "-" + String.format("%02d", current.getMonthValue());
            HealthReportDTO.MonthlyData md = new HealthReportDTO.MonthlyData();
            md.setMonth(key);
            monthMap.put(key, md);
            current = current.plusMonths(1);
        }

        for (WeightLog w : weightLogs) {
            if (w.getLogDate() == null) continue;
            String key = w.getLogDate().getYear() + "-" + String.format("%02d", w.getLogDate().getMonthValue());
            HealthReportDTO.MonthlyData md = monthMap.get(key);
            if (md == null) continue;
            md.setWeightRecords(md.getWeightRecords() + 1);
            if (md.getWeightFirst() == null) md.setWeightFirst(w.getWeightKg());
            md.setWeightLast(w.getWeightKg());
            if (md.getWeightMin() == null || w.getWeightKg().compareTo(md.getWeightMin()) < 0) md.setWeightMin(w.getWeightKg());
            if (md.getWeightMax() == null || w.getWeightKg().compareTo(md.getWeightMax()) > 0) md.setWeightMax(w.getWeightKg());
        }

        for (HealthEvents e : healthEvents) {
            if (e.getEventDate() == null) continue;
            String key = e.getEventDate().getYear() + "-" + String.format("%02d", e.getEventDate().getMonthValue());
            HealthReportDTO.MonthlyData md = monthMap.get(key);
            if (md == null) continue;
            md.setHealthEventsTotal(md.getHealthEventsTotal() + 1);
            if (e.getStatus() != null && e.getStatus() == 1) {
                md.setHealthEventsCompleted(md.getHealthEventsCompleted() + 1);
            }
        }

        for (FeedingRecord f : feedingRecords) {
            if (f.getFeedTime() == null) continue;
            String key = f.getFeedTime().getYear() + "-" + String.format("%02d", f.getFeedTime().getMonthValue());
            HealthReportDTO.MonthlyData md = monthMap.get(key);
            if (md == null) continue;
            md.setFeedingRecords(md.getFeedingRecords() + 1);
        }

        for (BathingRecord b : bathingRecords) {
            if (b.getBathTime() == null) continue;
            String key = b.getBathTime().getYear() + "-" + String.format("%02d", b.getBathTime().getMonthValue());
            HealthReportDTO.MonthlyData md = monthMap.get(key);
            if (md == null) continue;
            md.setBathingRecords(md.getBathingRecords() + 1);
        }

        for (MedicationRecord m : medicationRecords) {
            if (m.getStartDate() == null) continue;
            String key = m.getStartDate().getYear() + "-" + String.format("%02d", m.getStartDate().getMonthValue());
            HealthReportDTO.MonthlyData md = monthMap.get(key);
            if (md == null) continue;
            md.setMedicationRecords(md.getMedicationRecords() + 1);
        }

        report.setMonthlyData(new ArrayList<>(monthMap.values()));
        return report;
    }

    @Override
    public List<NotificationItemDTO> getNotificationSummary() {
        List<NotificationItemDTO> notifications = new ArrayList<>();
        List<Pets> allPets = this.list();
        if (allPets.isEmpty()) {
            return notifications;
        }

        // Read user notification preferences
        NotificationPrefsDTO prefs = null;
        try {
            long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
            prefs = getNotificationPrefs(userId);
        } catch (Exception ignored) {
            // Not logged in, use defaults
        }
        boolean healthEnabled = prefs == null || prefs.getHealthEnabled() == null || prefs.getHealthEnabled();
        boolean medicationEnabled = prefs == null || prefs.getMedicationEnabled() == null || prefs.getMedicationEnabled();
        boolean feedingEnabled = prefs == null || prefs.getFeedingEnabled() == null || prefs.getFeedingEnabled();
        boolean bathingEnabled = prefs == null || prefs.getBathingEnabled() == null || prefs.getBathingEnabled();
        boolean birthdayEnabled = prefs == null || prefs.getBirthdayEnabled() == null || prefs.getBirthdayEnabled();
        int healthDaysBefore = prefs != null && prefs.getHealthDaysBefore() != null ? prefs.getHealthDaysBefore() : 7;
        int birthdayDaysBefore = prefs != null && prefs.getBirthdayDaysBefore() != null ? prefs.getBirthdayDaysBefore() : 7;

        Map<Long, String> petNameMap = new java.util.HashMap<>();
        for (Pets pet : allPets) {
            petNameMap.put(pet.getId(), pet.getName());
        }

        LocalDate today = LocalDate.now();

        // 1. Health event reminders (overdue + upcoming within healthDaysBefore days)
        if (healthEnabled) {
            List<HealthEvents> upcomingEvents = healthEventsService.listUpcoming();
            for (HealthEvents event : upcomingEvents) {
                LocalDate dueDate = event.getNextDueDate();
                if (dueDate == null) continue;
                long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, dueDate);
                if (daysUntil > healthDaysBefore) continue;
                String petName = petNameMap.getOrDefault(event.getPetId(), "未知宠物");
                String eventLabel = "健康事件";
                try {
                    if (event.getEventTypeId() != null) {
                        DictItems item = dictItemsService.getById(event.getEventTypeId());
                        if (item != null) eventLabel = item.getItemLabel();
                    }
                } catch (Exception ignored) {}

                NotificationItemDTO dto = new NotificationItemDTO();
                dto.setType("health");
                dto.setPetId(event.getPetId());
                dto.setPetName(petName);
                dto.setSourceId(String.valueOf(event.getId()));
                dto.setPageTarget("health-events");

                if (daysUntil < 0) {
                    dto.setMessage(eventLabel + " 已过期 " + Math.abs(daysUntil) + " 天");
                    dto.setDetail(petName + " 的" + eventLabel + "需要处理");
                    dto.setUrgency(0);
                } else if (daysUntil == 0) {
                    dto.setMessage(eventLabel + " 今日到期");
                    dto.setDetail(petName + " 的" + eventLabel + "今天需要处理");
                    dto.setUrgency(1);
                } else if (daysUntil <= 3) {
                    dto.setMessage(eventLabel + " " + daysUntil + " 天后到期");
                    dto.setDetail(petName + " 的" + eventLabel + "即将到期");
                    dto.setUrgency(2);
                } else {
                    dto.setMessage(eventLabel + " " + daysUntil + " 天后到期");
                    dto.setDetail(petName + " 的" + eventLabel + "将在 " + daysUntil + " 天后到期");
                    dto.setUrgency(3);
                }
                notifications.add(dto);
            }
        }

        // 2. Active medication reminders
        if (medicationEnabled) {
            for (Pets pet : allPets) {
                List<MedicationRecord> meds = medicationRecordService.listByPetId(pet.getId());
                for (MedicationRecord med : meds) {
                    if (med.getEndDate() != null && med.getEndDate().isBefore(today)) continue;
                    long daysRemaining = med.getEndDate() != null ?
                        java.time.temporal.ChronoUnit.DAYS.between(today, med.getEndDate()) : 999;

                    NotificationItemDTO dto = new NotificationItemDTO();
                    dto.setType("medication");
                    dto.setPetId(pet.getId());
                    dto.setPetName(pet.getName());
                    dto.setSourceId(String.valueOf(med.getId()));
                    dto.setPageTarget("medication-records");

                    if (med.getEndDate() != null && daysRemaining <= 3) {
                        dto.setMessage("💊 " + med.getMedicationName() + " 即将结束");
                        dto.setDetail(med.getDosage() != null ? "剂量：" + med.getDosage() : "用药疗程即将完成");
                        dto.setUrgency(1);
                    } else {
                        dto.setMessage("💊 " + med.getMedicationName() + " 正在服用中");
                        dto.setDetail(med.getFrequency() != null ? "频率：" + med.getFrequency() : "请按时给药");
                        dto.setUrgency(3);
                    }
                    notifications.add(dto);
                }
            }
        }

        // 3. Feeding reminders (not fed today)
        if (feedingEnabled) {
            for (Pets pet : allPets) {
                List<FeedingRecord> feedings = feedingRecordService.listByPetId(pet.getId());
                boolean fedToday = feedings.stream().anyMatch(f ->
                    f.getFeedTime() != null && f.getFeedTime().toLocalDate().equals(today)
                );
                if (!fedToday && !feedings.isEmpty()) {
                    NotificationItemDTO dto = new NotificationItemDTO();
                    dto.setType("feeding");
                    dto.setPetId(pet.getId());
                    dto.setPetName(pet.getName());
                    dto.setMessage("🍽️ " + pet.getName() + " 今天还没有喂食记录");
                    dto.setDetail("记得给" + pet.getName() + "准备食物哦");
                    dto.setUrgency(2);
                    dto.setPageTarget("feeding-records");
                    notifications.add(dto);
                }
            }
        }

        // 4. Bathing reminders (not groomed in 30+ days)
        if (bathingEnabled) {
            for (Pets pet : allPets) {
                List<BathingRecord> baths = bathingRecordService.listByPetId(pet.getId());
                if (baths.isEmpty()) continue;
                OffsetDateTime lastBath = baths.stream()
                    .map(BathingRecord::getBathTime)
                    .filter(java.util.Objects::nonNull)
                    .max(OffsetDateTime::compareTo)
                    .orElse(null);
                if (lastBath != null) {
                    long daysSince = java.time.temporal.ChronoUnit.DAYS.between(
                        lastBath.toLocalDate(), today);
                    if (daysSince >= 30) {
                        NotificationItemDTO dto = new NotificationItemDTO();
                        dto.setType("bathing");
                        dto.setPetId(pet.getId());
                        dto.setPetName(pet.getName());
                        dto.setMessage("🛁 " + pet.getName() + " 已 " + daysSince + " 天未美容");
                        dto.setDetail("上次美容：" + lastBath.toLocalDate());
                        dto.setUrgency(daysSince >= 60 ? 1 : 3);
                        dto.setPageTarget("bathing-records");
                        notifications.add(dto);
                    }
                }
            }
        }

        // 5. Birthday reminders (birthday within next birthdayDaysBefore days)
        if (birthdayEnabled) {
            for (Pets pet : allPets) {
                if (pet.getBirthday() == null) continue;
                LocalDate thisYearBday;
                try {
                    thisYearBday = pet.getBirthday().withYear(today.getYear());
                } catch (java.time.DateTimeException e) {
                    thisYearBday = LocalDate.of(today.getYear(), 2, 28);
                }
                if (thisYearBday.isBefore(today)) {
                    try {
                        thisYearBday = pet.getBirthday().withYear(today.getYear() + 1);
                    } catch (java.time.DateTimeException e) {
                        thisYearBday = LocalDate.of(today.getYear() + 1, 2, 28);
                    }
                }
                long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, thisYearBday);
                if (daysUntil > birthdayDaysBefore) continue;

                int currentAge = today.getYear() - pet.getBirthday().getYear();
                if (today.getDayOfYear() < pet.getBirthday().getDayOfYear()) {
                    currentAge--;
                }
                int nextAge = currentAge + 1;

                NotificationItemDTO dto = new NotificationItemDTO();
                dto.setType("birthday");
                dto.setPetId(pet.getId());
                dto.setPetName(pet.getName());
                dto.setSourceId("bday-" + pet.getId());
                dto.setPageTarget("health-overview");

                if (daysUntil == 0) {
                    dto.setMessage("🎂 今天是" + pet.getName() + "的生日！");
                    dto.setDetail(pet.getName() + "今天满 " + nextAge + " 岁啦，生日快乐！🎉");
                    dto.setUrgency(0);
                } else if (daysUntil <= 3) {
                    dto.setMessage("🎂 " + pet.getName() + "的生日还有 " + daysUntil + " 天");
                    dto.setDetail(pet.getName() + "即将满 " + nextAge + " 岁");
                    dto.setUrgency(2);
                } else {
                    dto.setMessage("🎂 " + pet.getName() + "的生日还有 " + daysUntil + " 天");
                    dto.setDetail(pet.getName() + "将在 " + daysUntil + " 天后满 " + nextAge + " 岁");
                    dto.setUrgency(3);
                }
                notifications.add(dto);
            }
        }

        notifications.sort((a, b) -> {
            int cmp = Integer.compare(a.getUrgency(), b.getUrgency());
            if (cmp != 0) return cmp;
            return a.getType().compareTo(b.getType());
        });

        return notifications;
    }

    private static final String NOTIF_PREFS_KEY_PREFIX = "pets:notif_prefs:";

    @Override
    public NotificationPrefsDTO getNotificationPrefs(long userId) {
        try {
            String json = redisTemplate.opsForValue().get(NOTIF_PREFS_KEY_PREFIX + userId);
            if (json != null) {
                return objectMapper.readValue(json, NotificationPrefsDTO.class);
            }
        } catch (Exception e) {
            log.warn("Failed to read notification prefs for user {}: {}", userId, e.getMessage());
        }
        return null;
    }

    @Override
    public void saveNotificationPrefs(long userId, NotificationPrefsDTO prefs) {
        try {
            String json = objectMapper.writeValueAsString(prefs);
            redisTemplate.opsForValue().set(NOTIF_PREFS_KEY_PREFIX + userId, json);
        } catch (Exception e) {
            log.error("Failed to save notification prefs for user {}: {}", userId, e.getMessage());
        }
    }

    @Override
    // @Cacheable(value = "pets_by_species", key = "#species")
    public List<Pets> getPetsBySpecies(String species) {
        QueryWrapper<Pets> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("species", species);
        return this.list(queryWrapper);
    }

    @Override
    // @Cacheable(value = "pets_list")
    public List<Pets> list() {
        return super.list();
    }

    @Override
    @Cacheable(value = "pets_by_id", key = "#id")
    public Pets getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(value = {"pets_page", "pets_list", "pets_by_id", "pets_leaderboard", "pets_detail_by_id", "pets_by_species"}, allEntries = true)
    public boolean save(Pets entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = {"pets_page", "pets_list", "pets_by_id", "pets_leaderboard", "pets_detail_by_id", "pets_by_species"}, allEntries = true)
    public boolean updateById(Pets entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = {"pets_page", "pets_list", "pets_by_id", "pets_leaderboard", "pets_detail_by_id", "pets_by_species"}, allEntries = true)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public ActivityLogDTO getActivityLog(Long petId, String type, int pageNum, int pageSize) {
        ActivityLogDTO result = new ActivityLogDTO();
        List<ActivityLogDTO.ActivityLogItem> allActivities = new ArrayList<>();

        // 批量查询宠物名称
        List<Pets> allPets = this.list();
        Map<Long, String> petNameMap = allPets.stream()
                .collect(Collectors.toMap(Pets::getId, Pets::getName, (a, b) -> a));

        // 批量查询事件类型标签
        List<DictItems> allEventTypes = dictItemsService.list();
        Map<Long, String> eventTypeLabelMap = allEventTypes.stream()
                .collect(Collectors.toMap(DictItems::getId, DictItems::getItemLabel, (a, b) -> a));

        long weightCount = 0, healthCount = 0, feedingCount = 0;
        long photoCount = 0, medicationCount = 0, bathingCount = 0;

        // 1. 体重记录
        if (type == null || type.isEmpty() || "weight".equals(type)) {
            QueryWrapper<WeightLog> q = new QueryWrapper<>();
            if (petId != null) q.eq("pet_id", petId);
            q.orderByDesc("log_date");
            List<WeightLog> items = weightLogService.list(q);
            weightCount = items.size();
            for (WeightLog w : items) {
                ActivityLogDTO.ActivityLogItem item = new ActivityLogDTO.ActivityLogItem();
                item.setId("w-" + w.getId());
                item.setType("weight");
                item.setPetId(w.getPetId());
                item.setPetName(petNameMap.getOrDefault(w.getPetId(), "未知宠物"));
                item.setTitle("体重记录");
                item.setDetail(w.getWeightKg() != null ? w.getWeightKg() + " kg" : "");
                item.setDate(w.getLogDate() != null ? w.getLogDate().toString() : null);
                item.setIcon("⚖️");
                item.setColor("#0EA5E9");
                allActivities.add(item);
            }
        }

        // 2. 健康事件
        if (type == null || type.isEmpty() || "health".equals(type)) {
            QueryWrapper<HealthEvents> q = new QueryWrapper<>();
            if (petId != null) q.eq("pet_id", petId);
            q.orderByDesc("event_date");
            List<HealthEvents> items = healthEventsService.list(q);
            healthCount = items.size();
            for (HealthEvents e : items) {
                ActivityLogDTO.ActivityLogItem item = new ActivityLogDTO.ActivityLogItem();
                item.setId("h-" + e.getId());
                item.setType("health");
                item.setPetId(e.getPetId());
                item.setPetName(petNameMap.getOrDefault(e.getPetId(), "未知宠物"));
                item.setTitle("健康事件");
                item.setDetail(eventTypeLabelMap.getOrDefault(e.getEventTypeId(), ""));
                item.setDate(e.getEventDate() != null ? e.getEventDate().toString() : null);
                item.setIcon("🩺");
                item.setColor("#059669");
                allActivities.add(item);
            }
        }

        // 3. 喂养记录
        if (type == null || type.isEmpty() || "feeding".equals(type)) {
            QueryWrapper<FeedingRecord> q = new QueryWrapper<>();
            if (petId != null) q.eq("pet_id", petId);
            q.orderByDesc("feed_time");
            List<FeedingRecord> items = feedingRecordService.list(q);
            feedingCount = items.size();
            for (FeedingRecord f : items) {
                ActivityLogDTO.ActivityLogItem item = new ActivityLogDTO.ActivityLogItem();
                item.setId("f-" + f.getId());
                item.setType("feeding");
                item.setPetId(f.getPetId());
                item.setPetName(petNameMap.getOrDefault(f.getPetId(), "未知宠物"));
                item.setTitle("喂养记录");
                String amount = f.getAmountGrams() != null ? f.getAmountGrams() + "g" : "";
                String detail = f.getFoodType() != null ? f.getFoodType() : "";
                if (!amount.isEmpty()) detail += (detail.isEmpty() ? "" : " · ") + amount;
                item.setDetail(detail);
                item.setDate(f.getFeedTime() != null ? f.getFeedTime().toLocalDate().toString() : null);
                item.setIcon("🍽️");
                item.setColor("#D97706");
                allActivities.add(item);
            }
        }

        // 4. 相册照片
        if (type == null || type.isEmpty() || "photo".equals(type)) {
            QueryWrapper<PetGallery> q = new QueryWrapper<>();
            if (petId != null) q.eq("pet_id", petId);
            q.orderByDesc("created_at");
            List<PetGallery> items = petGalleryService.list(q);
            photoCount = items.size();
            for (PetGallery p : items) {
                ActivityLogDTO.ActivityLogItem item = new ActivityLogDTO.ActivityLogItem();
                item.setId("p-" + p.getId());
                item.setType("photo");
                item.setPetId(p.getPetId());
                item.setPetName(petNameMap.getOrDefault(p.getPetId(), "未知宠物"));
                item.setTitle("上传照片");
                item.setDetail(p.getDescription() != null ? p.getDescription() : "");
                item.setDate(p.getCreatedAt() != null ? p.getCreatedAt().toLocalDate().toString() : null);
                item.setIcon("📷");
                item.setColor("#9333EA");
                allActivities.add(item);
            }
        }

        // 5. 用药记录
        if (type == null || type.isEmpty() || "medication".equals(type)) {
            QueryWrapper<MedicationRecord> q = new QueryWrapper<>();
            if (petId != null) q.eq("pet_id", petId);
            q.orderByDesc("start_date");
            List<MedicationRecord> items = medicationRecordService.list(q);
            medicationCount = items.size();
            for (MedicationRecord m : items) {
                ActivityLogDTO.ActivityLogItem item = new ActivityLogDTO.ActivityLogItem();
                item.setId("m-" + m.getId());
                item.setType("medication");
                item.setPetId(m.getPetId());
                item.setPetName(petNameMap.getOrDefault(m.getPetId(), "未知宠物"));
                item.setTitle("用药记录");
                String dosage = m.getDosage() != null && !m.getDosage().isEmpty() ? " · " + m.getDosage() : "";
                item.setDetail(m.getMedicationName() + dosage);
                item.setDate(m.getStartDate() != null ? m.getStartDate().toString() : null);
                item.setIcon("💊");
                item.setColor("#7C3AED");
                allActivities.add(item);
            }
        }

        // 6. 洗澡美容
        if (type == null || type.isEmpty() || "bathing".equals(type)) {
            QueryWrapper<BathingRecord> q = new QueryWrapper<>();
            if (petId != null) q.eq("pet_id", petId);
            q.orderByDesc("bath_time");
            List<BathingRecord> items = bathingRecordService.list(q);
            bathingCount = items.size();
            for (BathingRecord b : items) {
                ActivityLogDTO.ActivityLogItem item = new ActivityLogDTO.ActivityLogItem();
                item.setId("b-" + b.getId());
                item.setType("bathing");
                item.setPetId(b.getPetId());
                item.setPetName(petNameMap.getOrDefault(b.getPetId(), "未知宠物"));
                item.setTitle("洗澡美容");
                item.setDetail(b.getServiceType() != null ? b.getServiceType() : "");
                item.setDate(b.getBathTime() != null ? b.getBathTime().toLocalDate().toString() : null);
                item.setIcon("🛁");
                item.setColor("#0891B2");
                allActivities.add(item);
            }
        }

        // 按日期降序排序
        allActivities.sort((a, b) -> {
            if (a.getDate() == null && b.getDate() == null) return 0;
            if (a.getDate() == null) return 1;
            if (b.getDate() == null) return -1;
            return b.getDate().compareTo(a.getDate());
        });

        // 统计
        ActivityLogDTO.ActivityStats stats = new ActivityLogDTO.ActivityStats();
        stats.setTotalActivities(allActivities.size());
        stats.setWeightCount(weightCount);
        stats.setHealthCount(healthCount);
        stats.setFeedingCount(feedingCount);
        stats.setPhotoCount(photoCount);
        stats.setMedicationCount(medicationCount);
        stats.setBathingCount(bathingCount);
        result.setStats(stats);
        result.setTotal(allActivities.size());

        // 分页
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allActivities.size());
        List<ActivityLogDTO.ActivityLogItem> pageItems = fromIndex < allActivities.size()
                ? allActivities.subList(fromIndex, toIndex)
                : new ArrayList<>();
        result.setActivities(pageItems);

        return result;
    }
}

package com.tox.tox.pets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.*;
import com.tox.tox.pets.mapper.PetsMapper;
import com.tox.tox.pets.model.dto.HealthEventsDTO;
import com.tox.tox.pets.model.dto.PetDetailDTO;
import com.tox.tox.pets.model.dto.PetPageDTO;
import com.tox.tox.pets.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import com.tox.tox.pets.model.dto.PetLeaderboardDTO;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Collections;
import java.util.stream.IntStream;

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
    @Autowired
    private LikingService likingService;

    @Autowired
    private IDictItemsService dictItemsService;

    @Autowired
    private IWeightLogService weightLogService;

    @Autowired
    private IHealthEventsService healthEventsService;

    /**
     * (❗ 核心实现)
     */
    @Override
    @Cacheable(value = "pets_page", key = "#pageNum + '-' + #pageSize")
    public IPage<PetPageDTO> findPetsWithLikes(int pageNum, int pageSize) {

        // 1. (DB) 创建 MP 分页对象
        IPage<Pets> petPageConfig = new Page<>(pageNum, pageSize);

        // 2. (DB) (❗) 执行 MP 分页查询
        // (this.page() 是 ServiceImpl 提供的)
        IPage<Pets> petPage = this.page(petPageConfig);

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
            PetPageDTO dto = new PetPageDTO(pet); // (使用我们上次定义的 PetPageDTO)
            long likeCount = likeCountsMap.getOrDefault(pet.getId(), 0L);
            dto.setLikeCount(likeCount);
            return dto;
        }).collect(Collectors.toList());

        // 6. (Java) (❗) 创建 DTO 分页结果
        // (重用 'petPage' 的分页元数据, 但用 'dtos' 替换 'records')
        IPage<PetPageDTO> dtoPage = new Page<>(petPage.getCurrent(), petPage.getSize(), petPage.getTotal());
        dtoPage.setRecords(dtos);

        return dtoPage;
    }

    @Override
    @Cacheable(value = "pets_leaderboard", key = "#topN")
    public List<PetLeaderboardDTO> getLeaderboard(int topN) {
        // 1. (Redis) 从 LikingService 获取排行榜 (ID 和分数)
        List<ZSetOperations.TypedTuple<String>> leaderboardFromRedis = likingService.getLeaderboard(topN);

        if (leaderboardFromRedis.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. (DB) 提取 Pet ID, 准备数据库批量查询
        List<Long> petIds = leaderboardFromRedis.stream()
                .map(tuple -> Long.parseLong(tuple.getValue()))
                .collect(Collectors.toList());

        // 3. (DB) 批量查询 Pet 的详细信息
        Map<Long, Pets> petsMap = this.listByIds(petIds).stream()
                .collect(Collectors.toMap(Pets::getId, pet -> pet));

        // 4. (Java) 组装成 DTO
        return IntStream.range(0, leaderboardFromRedis.size())
                .mapToObj(i -> {
                    ZSetOperations.TypedTuple<String> tuple = leaderboardFromRedis.get(i);
                    Long petId = Long.parseLong(tuple.getValue());
                    Pets pet = petsMap.get(petId);

                    // (健壮性) 如果 Redis 中的 petId 在数据库中找不到了, 跳过
                    if (pet == null) {
                        return null;
                    }

                    return new PetLeaderboardDTO(
                            i + 1, // Rank
                            petId,
                            pet.getName(),
                            tuple.getScore().longValue() // Like Count
                    );
                })
                .filter(dto -> dto != null) // 过滤掉没找到的
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

        return detailDTO;
    }

    @Override
    @Cacheable(value = "pets_by_species", key = "#species")
    public List<Pets> getPetsBySpecies(String species) {
        QueryWrapper<Pets> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("species", species);
        return this.list(queryWrapper);
    }

    @Override
    @Cacheable(value = "pets_list")
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
}

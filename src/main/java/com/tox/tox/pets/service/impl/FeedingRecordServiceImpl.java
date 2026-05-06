package com.tox.tox.pets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.FeedingRecord;
import com.tox.tox.pets.mapper.FeedingRecordMapper;
import com.tox.tox.pets.model.dto.FeedingStatsDTO;
import com.tox.tox.pets.service.IFeedingRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FeedingRecordServiceImpl extends ServiceImpl<FeedingRecordMapper, FeedingRecord> implements IFeedingRecordService {

    @Override
    @Cacheable(value = "feeding_records_by_pet", key = "#petId")
    public List<FeedingRecord> listByPetId(Long petId) {
        QueryWrapper<FeedingRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId);
        queryWrapper.orderByDesc("feed_time");
        return this.list(queryWrapper);
    }

    @Override
    public Page<FeedingRecord> pageByPetId(Integer pageNum, Integer pageSize, Long petId) {
        Page<FeedingRecord> page = new Page<>(pageNum, pageSize);
        QueryWrapper<FeedingRecord> queryWrapper = new QueryWrapper<>();
        if (petId != null) {
            queryWrapper.eq("pet_id", petId);
        }
        queryWrapper.orderByDesc("feed_time");
        return this.page(page, queryWrapper);
    }

    @Override
    public FeedingStatsDTO getFeedingStats(Long petId, Integer days) {
        QueryWrapper<FeedingRecord> qw = new QueryWrapper<>();
        if (petId != null) {
            qw.eq("pet_id", petId);
        }
        if (days != null && days > 0) {
            qw.ge("feed_time", OffsetDateTime.now().minusDays(days));
        }
        qw.orderByAsc("feed_time");
        List<FeedingRecord> records = this.list(qw);

        // Aggregate by date
        Map<String, List<FeedingRecord>> byDate = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getFeedTime().toLocalDate().toString(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<FeedingStatsDTO.DailyStat> dailyStats = new ArrayList<>();
        for (Map.Entry<String, List<FeedingRecord>> entry : byDate.entrySet()) {
            FeedingStatsDTO.DailyStat stat = new FeedingStatsDTO.DailyStat();
            stat.setDate(entry.getKey());
            stat.setCount(entry.getValue().size());
            int totalAmount = entry.getValue().stream()
                    .filter(r -> r.getAmountGrams() != null)
                    .mapToInt(FeedingRecord::getAmountGrams)
                    .sum();
            stat.setTotalAmount(totalAmount > 0 ? totalAmount : null);
            dailyStats.add(stat);
        }

        FeedingStatsDTO dto = new FeedingStatsDTO();
        dto.setDailyStats(dailyStats);
        dto.setTotalRecords(records.size());

        // Calculate avg daily amount
        long daysWithData = dailyStats.stream().filter(s -> s.getTotalAmount() != null && s.getTotalAmount() > 0).count();
        int totalAmountSum = dailyStats.stream()
                .filter(s -> s.getTotalAmount() != null)
                .mapToInt(FeedingStatsDTO.DailyStat::getTotalAmount)
                .sum();
        dto.setAvgDailyAmount(daysWithData > 0 ? Math.round((float) totalAmountSum / daysWithData) : null);

        return dto;
    }

    @Override
    @CacheEvict(value = {"feeding_records_by_pet", "pets_detail_by_id"}, allEntries = true)
    public boolean save(FeedingRecord entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = {"feeding_records_by_pet", "pets_detail_by_id"}, allEntries = true)
    public boolean updateById(FeedingRecord entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = {"feeding_records_by_pet", "pets_detail_by_id"}, allEntries = true)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}

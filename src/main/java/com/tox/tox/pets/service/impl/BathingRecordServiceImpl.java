package com.tox.tox.pets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tox.tox.pets.mapper.BathingRecordMapper;
import com.tox.tox.pets.model.BathingRecord;
import com.tox.tox.pets.model.dto.BathingStatsDTO;
import com.tox.tox.pets.service.IBathingRecordService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BathingRecordServiceImpl extends ServiceImpl<BathingRecordMapper, BathingRecord> implements IBathingRecordService {

    @Override
    @Cacheable(value = "bathing_records_by_pet", key = "#petId")
    public List<BathingRecord> listByPetId(Long petId) {
        QueryWrapper<BathingRecord> qw = new QueryWrapper<>();
        qw.eq("pet_id", petId);
        qw.orderByDesc("bath_time");
        return this.list(qw);
    }

    @Override
    public Page<BathingRecord> pageByPetId(Integer pageNum, Integer pageSize, Long petId) {
        Page<BathingRecord> page = new Page<>(pageNum, pageSize);
        QueryWrapper<BathingRecord> qw = new QueryWrapper<>();
        if (petId != null) {
            qw.eq("pet_id", petId);
        }
        qw.orderByDesc("bath_time");
        return this.page(page, qw);
    }

    @Override
    public BathingStatsDTO getBathingStats(Long petId, Integer days) {
        QueryWrapper<BathingRecord> qw = new QueryWrapper<>();
        if (petId != null) {
            qw.eq("pet_id", petId);
        }
        if (days != null && days > 0) {
            qw.ge("bath_time", OffsetDateTime.now().minusDays(days));
        }
        List<BathingRecord> records = this.list(qw);

        Map<String, Long> typeCountMap = records.stream()
                .filter(r -> r.getServiceType() != null && !r.getServiceType().isBlank())
                .collect(Collectors.groupingBy(BathingRecord::getServiceType, Collectors.counting()));

        List<BathingStatsDTO.TypeStat> typeStats = typeCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> {
                    BathingStatsDTO.TypeStat s = new BathingStatsDTO.TypeStat();
                    s.setServiceType(e.getKey());
                    s.setCount(e.getValue().intValue());
                    return s;
                })
                .collect(Collectors.toList());

        BathingStatsDTO dto = new BathingStatsDTO();
        dto.setTotalRecords(records.size());
        dto.setUniqueTypes(typeCountMap.size());
        dto.setTopType(typeStats.isEmpty() ? null : typeStats.get(0).getServiceType());
        dto.setTypeStats(typeStats);
        return dto;
    }

    @Override
    @CacheEvict(value = {"bathing_records_by_pet", "pets_detail_by_id"}, allEntries = true)
    public boolean save(BathingRecord entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = {"bathing_records_by_pet", "pets_detail_by_id"}, allEntries = true)
    public boolean updateById(BathingRecord entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = {"bathing_records_by_pet", "pets_detail_by_id"}, allEntries = true)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}

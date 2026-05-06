package com.tox.tox.pets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tox.tox.pets.mapper.MedicationRecordMapper;
import com.tox.tox.pets.model.MedicationRecord;
import com.tox.tox.pets.model.dto.MedicationStatsDTO;
import com.tox.tox.pets.service.IMedicationRecordService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MedicationRecordServiceImpl extends ServiceImpl<MedicationRecordMapper, MedicationRecord> implements IMedicationRecordService {

    @Override
    @Cacheable(value = "medication_records_by_pet", key = "#petId")
    public List<MedicationRecord> listByPetId(Long petId) {
        QueryWrapper<MedicationRecord> qw = new QueryWrapper<>();
        qw.eq("pet_id", petId);
        qw.orderByDesc("start_date");
        return this.list(qw);
    }

    @Override
    public Page<MedicationRecord> pageByPetId(Integer pageNum, Integer pageSize, Long petId) {
        Page<MedicationRecord> page = new Page<>(pageNum, pageSize);
        QueryWrapper<MedicationRecord> qw = new QueryWrapper<>();
        if (petId != null) {
            qw.eq("pet_id", petId);
        }
        qw.orderByDesc("start_date");
        return this.page(page, qw);
    }

    @Override
    public MedicationStatsDTO getMedicationStats(Long petId, Integer days) {
        QueryWrapper<MedicationRecord> qw = new QueryWrapper<>();
        if (petId != null) {
            qw.eq("pet_id", petId);
        }
        if (days != null && days > 0) {
            qw.ge("created_at", OffsetDateTime.now().minusDays(days));
        }
        List<MedicationRecord> records = this.list(qw);

        LocalDate today = LocalDate.now();
        long activeCount = records.stream()
                .filter(r -> r.getStartDate() != null && !r.getStartDate().isAfter(today)
                        && (r.getEndDate() == null || !r.getEndDate().isBefore(today)))
                .count();
        long completedCount = records.stream()
                .filter(r -> r.getEndDate() != null && r.getEndDate().isBefore(today))
                .count();

        Map<String, Long> typeCountMap = records.stream()
                .filter(r -> r.getMedicationType() != null && !r.getMedicationType().isBlank())
                .collect(Collectors.groupingBy(MedicationRecord::getMedicationType, Collectors.counting()));

        List<MedicationStatsDTO.TypeStat> typeStats = typeCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> {
                    MedicationStatsDTO.TypeStat s = new MedicationStatsDTO.TypeStat();
                    s.setMedicationType(e.getKey());
                    s.setCount(e.getValue().intValue());
                    return s;
                })
                .collect(Collectors.toList());

        MedicationStatsDTO dto = new MedicationStatsDTO();
        dto.setTotalRecords(records.size());
        dto.setActiveCount(activeCount);
        dto.setCompletedCount(completedCount);
        dto.setTopType(typeStats.isEmpty() ? null : typeStats.get(0).getMedicationType());
        dto.setTypeStats(typeStats);
        return dto;
    }

    @Override
    @CacheEvict(value = {"medication_records_by_pet", "pets_detail_by_id"}, allEntries = true)
    public boolean save(MedicationRecord entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = {"medication_records_by_pet", "pets_detail_by_id"}, allEntries = true)
    public boolean updateById(MedicationRecord entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = {"medication_records_by_pet", "pets_detail_by_id"}, allEntries = true)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}

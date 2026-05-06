package com.tox.tox.pets.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tox.tox.pets.model.MedicationRecord;
import com.tox.tox.pets.model.dto.MedicationStatsDTO;

import java.util.List;

public interface IMedicationRecordService extends IService<MedicationRecord> {
    List<MedicationRecord> listByPetId(Long petId);
    Page<MedicationRecord> pageByPetId(Integer pageNum, Integer pageSize, Long petId);
    MedicationStatsDTO getMedicationStats(Long petId, Integer days);
}

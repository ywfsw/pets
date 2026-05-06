package com.tox.tox.pets.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tox.tox.pets.model.BathingRecord;

import java.util.List;

public interface IBathingRecordService extends IService<BathingRecord> {
    List<BathingRecord> listByPetId(Long petId);
    Page<BathingRecord> pageByPetId(Integer pageNum, Integer pageSize, Long petId);
}

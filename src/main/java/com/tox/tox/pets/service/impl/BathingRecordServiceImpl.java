package com.tox.tox.pets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tox.tox.pets.mapper.BathingRecordMapper;
import com.tox.tox.pets.model.BathingRecord;
import com.tox.tox.pets.service.IBathingRecordService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

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

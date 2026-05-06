package com.tox.tox.pets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.FeedingRecord;
import com.tox.tox.pets.mapper.FeedingRecordMapper;
import com.tox.tox.pets.service.IFeedingRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

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

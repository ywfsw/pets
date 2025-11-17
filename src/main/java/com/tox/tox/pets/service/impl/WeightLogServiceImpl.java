package com.tox.tox.pets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tox.tox.pets.model.WeightLog;
import com.tox.tox.pets.mapper.WeightLogMapper;
import com.tox.tox.pets.service.IWeightLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 宠物体重记录表 (一对多) 服务实现类
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Service
public class WeightLogServiceImpl extends ServiceImpl<WeightLogMapper, WeightLog> implements IWeightLogService {

    @Override
    @Cacheable(value = "weight_log_by_pet", key = "#petId")
    public List<WeightLog> listByPetId(Long petId) {
        QueryWrapper<WeightLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId);
        queryWrapper.orderByDesc("log_date");
        return this.list(queryWrapper);
    }

    @Override
    @Cacheable(value = "weight_log_latest_by_pet", key = "#petId")
    public WeightLog getLatestByPetId(Long petId) {
        QueryWrapper<WeightLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId);
        queryWrapper.orderByDesc("log_date");
        queryWrapper.last("LIMIT 1");
        List<WeightLog> logs = this.list(queryWrapper);
        return logs.isEmpty() ? null : logs.get(0);
    }

    @Override
    @CacheEvict(value = {"weight_log_by_pet", "weight_log_latest_by_pet"}, allEntries = true)
    public boolean save(WeightLog entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = {"weight_log_by_pet", "weight_log_latest_by_pet"}, allEntries = true)
    public boolean updateById(WeightLog entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = {"weight_log_by_pet", "weight_log_latest_by_pet"}, allEntries = true)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}

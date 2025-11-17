package com.tox.tox.pets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tox.tox.pets.model.HealthEvents;
import com.tox.tox.pets.mapper.HealthEventsMapper;
import com.tox.tox.pets.service.IHealthEventsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 宠物健康事件表 (核心提醒功能) 服务实现类
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Service
public class HealthEventsServiceImpl extends ServiceImpl<HealthEventsMapper, HealthEvents> implements IHealthEventsService {

    @Override
    @Cacheable(value = "health_events_by_pet", key = "#petId")
    public List<HealthEvents> listByPetId(Long petId) {
        QueryWrapper<HealthEvents> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId);
        queryWrapper.orderByDesc("event_date");
        return this.list(queryWrapper);
    }

    @Override
    @Cacheable("health_events_upcoming")
    public List<HealthEvents> listUpcoming() {
        QueryWrapper<HealthEvents> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("next_due_date");
        queryWrapper.le("next_due_date", java.time.LocalDate.now().plusDays(7));
        queryWrapper.orderByAsc("next_due_date");
        return this.list(queryWrapper);
    }

    @Override
    @CacheEvict(value = {"health_events_by_pet", "health_events_upcoming"}, allEntries = true)
    public boolean save(HealthEvents entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = {"health_events_by_pet", "health_events_upcoming"}, allEntries = true)
    public boolean updateById(HealthEvents entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = {"health_events_by_pet", "health_events_upcoming"}, allEntries = true)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}

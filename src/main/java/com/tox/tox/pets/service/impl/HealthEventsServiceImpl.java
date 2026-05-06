package com.tox.tox.pets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.DictItems;
import com.tox.tox.pets.model.HealthEvents;
import com.tox.tox.pets.model.dto.HealthEventsStatsDTO;
import com.tox.tox.pets.mapper.HealthEventsMapper;
import com.tox.tox.pets.service.IDictItemsService;
import com.tox.tox.pets.service.IHealthEventsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private IDictItemsService dictItemsService;

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
        queryWrapper.and(w -> w.eq("status", 0).or().isNull("status"));
        queryWrapper.le("next_due_date", java.time.LocalDate.now().plusDays(7));
        queryWrapper.orderByAsc("next_due_date");
        return this.list(queryWrapper);
    }

    @Override
    @CacheEvict(value = {"health_events_by_pet", "health_events_upcoming"}, allEntries = true)
    public boolean completeEvent(Long id) {
        HealthEvents event = this.getById(id);
        if (event == null) {
            return false;
        }
        event.setStatus(1);
        return this.updateById(event);
    }

    @Override
    @CacheEvict(value = {"health_events_by_pet", "health_events_upcoming"}, allEntries = true)
    public boolean uncompleteEvent(Long id) {
        HealthEvents event = this.getById(id);
        if (event == null) {
            return false;
        }
        event.setStatus(0);
        return this.updateById(event);
    }

    @Override
    @CacheEvict(value = {"health_events_by_pet", "health_events_upcoming", "pets_detail_by_id"}, allEntries = true)
    public boolean save(HealthEvents entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = {"health_events_by_pet", "health_events_upcoming", "pets_detail_by_id"}, allEntries = true)
    public boolean updateById(HealthEvents entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = {"health_events_by_pet", "health_events_upcoming", "pets_detail_by_id"}, allEntries = true)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public Page<HealthEvents> pageByPetId(Integer pageNum, Integer pageSize, Long petId, Integer status) {
        Page<HealthEvents> page = new Page<>(pageNum, pageSize);
        QueryWrapper<HealthEvents> queryWrapper = new QueryWrapper<>();
        if (petId != null) {
            queryWrapper.eq("pet_id", petId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("event_date");
        return this.page(page, queryWrapper);
    }

    @Override
    public HealthEventsStatsDTO getHealthEventsStats(Long petId) {
        HealthEventsStatsDTO stats = new HealthEventsStatsDTO();

        QueryWrapper<HealthEvents> baseQuery = new QueryWrapper<>();
        if (petId != null) {
            baseQuery.eq("pet_id", petId);
        }
        List<HealthEvents> allEvents = this.list(baseQuery);

        LocalDate today = LocalDate.now();
        long pending = 0;
        long completed = 0;
        long overdue = 0;
        Map<Long, Long> typeCountMap = new HashMap<>();

        for (HealthEvents event : allEvents) {
            boolean isCompleted = Objects.equals(event.getStatus(), 1);
            if (isCompleted) {
                completed++;
            } else {
                pending++;
                if (event.getNextDueDate() != null && event.getNextDueDate().isBefore(today)) {
                    overdue++;
                }
            }
            if (event.getEventTypeId() != null) {
                typeCountMap.merge(event.getEventTypeId(), 1L, Long::sum);
            }
        }

        stats.setTotalCount(allEvents.size());
        stats.setPendingCount(pending);
        stats.setCompletedCount(completed);
        stats.setOverdueCount(overdue);

        // 批量查询事件类型名称
        List<HealthEventsStatsDTO.TypeBreakdown> breakdown = new ArrayList<>();
        if (!typeCountMap.isEmpty()) {
            List<DictItems> typeItems = dictItemsService.listByIds(typeCountMap.keySet());
            Map<Long, String> typeNameMap = typeItems.stream()
                    .collect(Collectors.toMap(DictItems::getId, DictItems::getItemLabel, (a, b) -> a));
            for (Map.Entry<Long, Long> entry : typeCountMap.entrySet()) {
                HealthEventsStatsDTO.TypeBreakdown item = new HealthEventsStatsDTO.TypeBreakdown();
                item.setEventTypeId(entry.getKey());
                item.setEventTypeName(typeNameMap.getOrDefault(entry.getKey(), "未知类型"));
                item.setCount(entry.getValue());
                breakdown.add(item);
            }
            breakdown.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));
        }
        stats.setTypeBreakdown(breakdown);

        return stats;
    }
}

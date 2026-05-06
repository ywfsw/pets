package com.tox.tox.pets.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.HealthEvents;
import com.tox.tox.pets.model.dto.HealthEventsStatsDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 宠物健康事件表 (核心提醒功能) 服务类
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
public interface IHealthEventsService extends IService<HealthEvents> {

    List<HealthEvents> listByPetId(Long petId);

    List<HealthEvents> listUpcoming();

    boolean completeEvent(Long id);

    boolean uncompleteEvent(Long id);

    Page<HealthEvents> pageByPetId(Integer pageNum, Integer pageSize, Long petId, Integer status);

    HealthEventsStatsDTO getHealthEventsStats(Long petId);
}

package com.tox.tox.pets.service.impl;

import com.tox.tox.pets.model.HealthEvents;
import com.tox.tox.pets.mapper.HealthEventsMapper;
import com.tox.tox.pets.service.IHealthEventsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}

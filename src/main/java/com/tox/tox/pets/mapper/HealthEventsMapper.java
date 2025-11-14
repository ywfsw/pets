package com.tox.tox.pets.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tox.tox.pets.model.HealthEvents;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 宠物健康事件表 (核心提醒功能) Mapper 接口
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Mapper
public interface HealthEventsMapper extends BaseMapper<HealthEvents> {

}

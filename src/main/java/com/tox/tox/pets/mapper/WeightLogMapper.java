package com.tox.tox.pets.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tox.tox.pets.model.WeightLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 宠物体重记录表 (一对多) Mapper 接口
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Mapper
public interface WeightLogMapper extends BaseMapper<WeightLog> {

}

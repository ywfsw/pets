package com.tox.tox.pets.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tox.tox.pets.model.DictTypes;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 字典类型主表 (用于管理所有字典分组) Mapper 接口
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Mapper
public interface DictTypesMapper extends BaseMapper<DictTypes> {

}

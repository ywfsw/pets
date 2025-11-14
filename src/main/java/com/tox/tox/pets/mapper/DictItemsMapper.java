package com.tox.tox.pets.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tox.tox.pets.model.DictItems;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 通用字典项表 Mapper 接口
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Mapper
public interface DictItemsMapper extends BaseMapper<DictItems> {

    List<DictItems> selectLookupByCode(String dictCode);
}

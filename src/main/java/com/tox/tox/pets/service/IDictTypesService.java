package com.tox.tox.pets.service;

import com.tox.tox.pets.model.DictTypes;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 字典类型主表 (用于管理所有字典分组) 服务类
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
public interface IDictTypesService extends IService<DictTypes> {
    List<DictTypes> listByParentCode(String parentCode);
}

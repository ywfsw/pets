package com.tox.tox.pets.service;

import com.tox.tox.pets.model.DictItems;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tox.tox.pets.model.dto.DictItemLookupDTO;

import java.util.List;

/**
 * <p>
 * 通用字典项表 服务类
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
public interface IDictItemsService extends IService<DictItems> {

    List<DictItemLookupDTO> findLookupByCode(String dictCode);
}

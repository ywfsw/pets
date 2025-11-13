package com.tox.tox.pets.service.impl;

import com.tox.tox.pets.model.DictTypes;
import com.tox.tox.pets.mapper.DictTypesMapper;
import com.tox.tox.pets.service.IDictTypesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 字典类型主表 (用于管理所有字典分组) 服务实现类
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Service
public class DictTypesServiceImpl extends ServiceImpl<DictTypesMapper, DictTypes> implements IDictTypesService {

}

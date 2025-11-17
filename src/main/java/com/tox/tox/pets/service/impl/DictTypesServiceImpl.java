package com.tox.tox.pets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.DictTypes;
import com.tox.tox.pets.mapper.DictTypesMapper;
import com.tox.tox.pets.service.IDictTypesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

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

    @Override
    @Cacheable(value = "dict_types", key = "#page.current + '-' + #page.size")
    public <E extends IPage<DictTypes>> E page(E page) {
        return super.page(page);
    }

    @Override
    @Cacheable(value = "dict_types", key = "'list'")
    public List<DictTypes> list() {
        return super.list();
    }

    @Override
    @Cacheable(value = "dict_types", key = "#id")
    public DictTypes getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(value = "dict_types_by_parent", key = "#parentCode")
    public List<DictTypes> listByParentCode(String parentCode) {
        QueryWrapper<DictTypes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_code", parentCode);
        return this.list(queryWrapper);
    }

    @Override
    @CacheEvict(value = {"dict_types", "dict_types_by_parent"}, allEntries = true)
    public boolean save(DictTypes entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = {"dict_types", "dict_types_by_parent"}, allEntries = true)
    public boolean updateById(DictTypes entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = {"dict_types", "dict_types_by_parent"}, allEntries = true)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}

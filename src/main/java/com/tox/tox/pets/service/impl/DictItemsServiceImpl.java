package com.tox.tox.pets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tox.tox.pets.model.DictItems;
import com.tox.tox.pets.mapper.DictItemsMapper;
import com.tox.tox.pets.model.dto.DictItemLookupDTO;
import com.tox.tox.pets.service.IDictItemsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 通用字典项表 服务实现类
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Service
public class DictItemsServiceImpl extends ServiceImpl<DictItemsMapper, DictItems> implements IDictItemsService {

    @Autowired
    private DictItemsMapper dictItemsMapper;

    @Override
    @Cacheable(value = "dict_items", key = "#dictCode")
    public List<DictItemLookupDTO> findLookupByCode(String dictCode) {
        // (推荐) Mapper.xml 中专门写一个 SQL, 只 SELECT id, label
        // (偷懒) 也可以用已有的 Mapper, 在 Service 层转换 DTO

        // 假设 Mapper 返回的是 POJO (DictItem)
        List<DictItems> items = dictItemsMapper.selectLookupByCode(dictCode);

        // 使用 Stream API 转换为 DTO (Rule 3.1)
        return items.stream()
                .map(this::convertToLookupDTO)
                .collect(Collectors.toList());
    }

    private DictItemLookupDTO convertToLookupDTO(DictItems entity) {
        DictItemLookupDTO dto = new DictItemLookupDTO();
        dto.setId(entity.getId());
        dto.setLabel(entity.getItemLabel());
        return dto;
    }

    @Override
    @Cacheable(value = "dict_items_by_code", key = "#dictCode")
    public List<DictItems> listByDictCode(String dictCode) {
        QueryWrapper<DictItems> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_code", dictCode);
        queryWrapper.orderByAsc("sort_order", "id");
        return this.list(queryWrapper);
    }

    @Override
    @Cacheable(value = "dict_items", key = "#page.current + '-' + #page.size")
    public <E extends IPage<DictItems>> E page(E page) {
        return super.page(page);
    }

    @Override
    @Cacheable(value = "dict_items", key = "'list'")
    public List<DictItems> list() {
        return super.list();
    }

    @Override
    @Cacheable(value = "dict_items", key = "#id")
    public DictItems getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(value = {"dict_items", "dict_items_by_code"}, allEntries = true)
    public boolean save(DictItems entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = {"dict_items", "dict_items_by_code"}, allEntries = true)
    public boolean updateById(DictItems entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = {"dict_items", "dict_items_by_code"}, allEntries = true)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}

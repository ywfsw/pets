package com.tox.tox.pets.service.impl;

import com.tox.tox.pets.model.DictItems;
import com.tox.tox.pets.mapper.DictItemsMapper;
import com.tox.tox.pets.model.dto.DictItemLookupDTO;
import com.tox.tox.pets.service.IDictItemsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

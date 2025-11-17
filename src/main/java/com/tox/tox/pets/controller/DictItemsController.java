package com.tox.tox.pets.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.DictItems;
import com.tox.tox.pets.model.dto.DictItemLookupDTO;
import com.tox.tox.pets.service.IDictItemsService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * <p>
 * 通用字典项表 前端控制器
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@RestController
@RequestMapping("/api")
public class DictItemsController {

    @Autowired
    private IDictItemsService dictItemsService;

    /**
     * 添加字典项
     */
    @PostMapping("/dictItems")
    public ResponseEntity<String> addDictItem(@RequestBody DictItems dictItem) {
        // 设置创建时间，使用OffsetDateTime
        dictItem.setCreatedAt(OffsetDateTime.now());
        boolean saved = dictItemsService.save(dictItem);
        if (saved) {
            return ResponseEntity.status(HttpStatus.CREATED).body("字典项添加成功，ID：" + dictItem.getId());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("字典项添加失败");
        }
    }

    /**
     * 获取字典项列表
     */
    @GetMapping("/dictItems")
    public ResponseEntity<List<DictItems>> listDictItems() {
        List<DictItems> dictItems = dictItemsService.list();
        return ResponseEntity.ok(dictItems);
    }

    /**
     * 分页查询字典项
     */
    @GetMapping("/dictItems/page")
    public ResponseEntity<Page<DictItems>> pageDictItems(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<DictItems> page = new Page<>(pageNum, pageSize);
        Page<DictItems> resultPage = dictItemsService.page(page);
        return ResponseEntity.ok(resultPage);
    }

    /**
     * 根据ID获取字典项
     */
    @GetMapping("/dictItems/{id}")
    public ResponseEntity<DictItems> getDictItemById(@PathVariable Long id) {
        DictItems dictItem = dictItemsService.getById(id);
        if (dictItem != null) {
            return ResponseEntity.ok(dictItem);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 根据ID更新字典项
     */
    @PutMapping("/dictItems/{id}")
    public ResponseEntity<String> updateDictItem(@PathVariable Long id, @RequestBody DictItems dictItem) {
        // 确保ID一致
        dictItem.setId(id);
        // 不更新创建时间
        DictItems existingItem = dictItemsService.getById(id);
        if (existingItem != null) {
            dictItem.setCreatedAt(existingItem.getCreatedAt());
            boolean updated = dictItemsService.updateById(dictItem);
            if (updated) {
                return ResponseEntity.ok("字典项更新成功，ID：" + id);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("字典项更新失败");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("字典项不存在，ID：" + id);
        }
    }

    /**
     * 根据ID删除字典项
     */
    @DeleteMapping("/dictItems/{id}")
    public ResponseEntity<String> deleteDictItem(@PathVariable Long id) {
        boolean deleted = dictItemsService.removeById(id);
        if (deleted) {
            return ResponseEntity.ok("字典项删除成功，ID：" + id);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("字典项不存在，ID：" + id);
        }
    }

    /**
     * 根据字典编码获取字典项列表
     */
    @GetMapping("/dictItems/code/{dictCode}")
    public ResponseEntity<List<DictItems>> getDictItemsByCode(@PathVariable String dictCode) {
        List<DictItems> dictItems = dictItemsService.listByDictCode(dictCode);
        return ResponseEntity.ok(dictItems);
    }

    /**
     * 【新】查找接口 (用于父级ID下拉选择器)
     *
     * @param dictCode 必须传入的字典类型编码 (e.g., "PET_SPECIES")
     * @return 字典项列表 (非分页)
     */
    @GetMapping("/dictItems/lookup")
    public ResponseEntity<List<DictItemLookupDTO>> getDictItemLookup(
            @RequestParam("dictCode") String dictCode) {

        // 2. 调用 Service, Service 会调用 Mapper
        List<DictItemLookupDTO> results = dictItemsService.findLookupByCode(dictCode);

        return ResponseEntity.ok(results);
    }
}
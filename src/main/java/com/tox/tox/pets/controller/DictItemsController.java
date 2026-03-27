package com.tox.tox.pets.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.DictItems;
import com.tox.tox.pets.model.dto.DictItemLookupDTO;
import com.tox.tox.pets.service.IDictItemsService;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "字典项管理", description = "字典项相关的增删改查接口")
public class DictItemsController {

    @Autowired
    private IDictItemsService dictItemsService;

    /**
     * 添加字典项
     */
    @PostMapping("/dictItems")
    @Operation(summary = "添加字典项", description = "创建一个新的字典项")
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
    @Operation(summary = "获取字典项列表", description = "获取所有字典项列表")
    public ResponseEntity<List<DictItems>> listDictItems() {
        List<DictItems> dictItems = dictItemsService.list();
        return ResponseEntity.ok(dictItems);
    }

    /**
     * 分页查询字典项
     */
    @GetMapping("/dictItems/page")
    @Operation(summary = "分页查询字典项", description = "分页获取字典项列表")
    public ResponseEntity<Page<DictItems>> pageDictItems(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<DictItems> page = new Page<>(pageNum, pageSize);
        Page<DictItems> resultPage = dictItemsService.page(page);
        return ResponseEntity.ok(resultPage);
    }

    /**
     * 根据ID获取字典项
     */
    @GetMapping("/dictItems/{id}")
    @Operation(summary = "获取字典项详情", description = "根据ID获取字典项")
    public ResponseEntity<DictItems> getDictItemById(@Parameter(description = "字典项ID") @PathVariable Long id) {
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
    @Operation(summary = "更新字典项", description = "根据ID更新字典项")
    public ResponseEntity<String> updateDictItem(@Parameter(description = "字典项ID") @PathVariable Long id, @RequestBody DictItems dictItem) {
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
    @Operation(summary = "删除字典项", description = "根据ID删除字典项")
    public ResponseEntity<String> deleteDictItem(@Parameter(description = "字典项ID") @PathVariable Long id) {
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
    @Operation(summary = "根据字典编码获取字典项", description = "获取指定字典编码下的所有字典项")
    public ResponseEntity<List<DictItems>> getDictItemsByCode(@Parameter(description = "字典编码") @PathVariable String dictCode) {
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
    @Operation(summary = "字典项查找", description = "用于父级ID下拉选择器，根据字典类型编码获取字典项列表")
    public ResponseEntity<List<DictItemLookupDTO>> getDictItemLookup(
            @Parameter(description = "字典类型编码", required = true) @RequestParam("dictCode") String dictCode) {

        // 2. 调用 Service, Service 会调用 Mapper
        List<DictItemLookupDTO> results = dictItemsService.findLookupByCode(dictCode);

        return ResponseEntity.ok(results);
    }
}
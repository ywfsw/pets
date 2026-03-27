package com.tox.tox.pets.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.DictTypes;
import com.tox.tox.pets.service.IDictTypesService;
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
 * 字典类型主表 (用于管理所有字典分组) 前端控制器
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@RestController
@RequestMapping("/api")
@Tag(name = "字典类型管理", description = "字典类型相关的增删改查接口")
public class DictTypesController {

    @Autowired
    private IDictTypesService dictTypesService;

    /**
     * 添加字典类型
     */
    @PostMapping("/dictTypes")
    @Operation(summary = "添加字典类型", description = "创建一个新的字典类型")
    public ResponseEntity<String> addDictType(@RequestBody DictTypes dictType) {
        // 设置创建时间
        dictType.setCreatedAt(OffsetDateTime.now());
        boolean saved = dictTypesService.save(dictType);
        if (saved) {
            return ResponseEntity.status(HttpStatus.CREATED).body("字典类型添加成功，编码：" + dictType.getDictCode());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("字典类型添加失败");
        }
    }

    /**
     * 获取字典类型列表
     */
    @GetMapping("/dictTypes")
    @Operation(summary = "获取字典类型列表", description = "获取所有字典类型列表")
    public ResponseEntity<List<DictTypes>> listDictTypes() {
        List<DictTypes> dictTypes = dictTypesService.list();
        return ResponseEntity.ok(dictTypes);
    }

    /**
     * 分页查询字典类型
     */
    @GetMapping("/dictTypes/page")
    @Operation(summary = "分页查询字典类型", description = "分页获取字典类型列表")
    public ResponseEntity<Page<DictTypes>> pageDictTypes(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<DictTypes> page = new Page<>(pageNum, pageSize);
        Page<DictTypes> resultPage = dictTypesService.page(page);
        return ResponseEntity.ok(resultPage);
    }

    /**
     * 根据字典编码获取字典类型
     */
    @GetMapping("/dictTypes/{dictCode}")
    @Operation(summary = "获取字典类型详情", description = "根据字典编码获取字典类型")
    public ResponseEntity<DictTypes> getDictTypeByCode(@Parameter(description = "字典编码") @PathVariable String dictCode) {
        DictTypes dictType = dictTypesService.getById(dictCode);
        if (dictType != null) {
            return ResponseEntity.ok(dictType);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 根据父级编码获取字典类型列表
     */
    @GetMapping("/dictTypes/parent/{parentCode}")
    @Operation(summary = "根据父级编码获取字典类型", description = "获取指定父级编码下的所有字典类型")
    public ResponseEntity<List<DictTypes>> getDictTypesByParentCode(@Parameter(description = "父级编码") @PathVariable String parentCode) {
        List<DictTypes> dictTypes = dictTypesService.listByParentCode(parentCode);
        return ResponseEntity.ok(dictTypes);
    }

    /**
     * 更新字典类型
     */
    @PutMapping("/dictTypes/{dictCode}")
    @Operation(summary = "更新字典类型", description = "根据字典编码更新字典类型")
    public ResponseEntity<String> updateDictType(@Parameter(description = "字典编码") @PathVariable String dictCode, @RequestBody DictTypes dictType) {
        // 确保字典编码一致
        dictType.setDictCode(dictCode);
        // 不更新创建时间
        DictTypes existingType = dictTypesService.getById(dictCode);
        if (existingType != null) {
            dictType.setCreatedAt(existingType.getCreatedAt());
            boolean updated = dictTypesService.updateById(dictType);
            if (updated) {
                return ResponseEntity.ok("字典类型更新成功，编码：" + dictCode);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("字典类型更新失败");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("字典类型不存在，编码：" + dictCode);
        }
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/dictTypes/{dictCode}")
    @Operation(summary = "删除字典类型", description = "根据字典编码删除字典类型")
    public ResponseEntity<String> deleteDictType(@Parameter(description = "字典编码") @PathVariable String dictCode) {
        boolean deleted = dictTypesService.removeById(dictCode);
        if (deleted) {
            return ResponseEntity.ok("字典类型删除成功，编码：" + dictCode);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("字典类型不存在，编码：" + dictCode);
        }
    }
}
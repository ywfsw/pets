package com.tox.tox.pets.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tox.tox.pets.model.PetGallery;
import com.tox.tox.pets.service.IPetGalleryService;
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
 * 存储宠物的相册图片 (多张，与 pets 表一对多关联) 前端控制器
 * </p>
 *
 * @author tox
 * @since 2025-11-15
 */
@RestController
@RequestMapping("/api")
@Tag(name = "宠物相册管理", description = "宠物相册图片相关的增删改查接口")
public class PetGalleryController {

    @Autowired
    private IPetGalleryService petGalleryService;

    /**
     * 添加相册图片
     */
    @PostMapping("/petGallery")
    @Operation(summary = "添加相册图片", description = "添加一张宠物相册图片")
    public ResponseEntity<String> addPetGallery(@RequestBody PetGallery petGallery) {
        petGallery.setCreatedAt(OffsetDateTime.now());
        boolean saved = petGalleryService.save(petGallery);
        if (saved) {
            return ResponseEntity.status(HttpStatus.CREATED).body("相册图片添加成功，ID：" + petGallery.getId());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("相册图片添加失败");
        }
    }

    /**
     * 获取所有相册图片
     */
    @GetMapping("/petGallery")
    @Operation(summary = "获取所有相册图片", description = "获取所有宠物的相册图片列表")
    public ResponseEntity<List<PetGallery>> listPetGalleries() {
        List<PetGallery> list = petGalleryService.list();
        return ResponseEntity.ok(list);
    }

    /**
     * 根据ID获取相册图片
     */
    @GetMapping("/petGallery/{id}")
    @Operation(summary = "获取相册图片详情", description = "根据ID获取相册图片")
    public ResponseEntity<PetGallery> getPetGalleryById(@Parameter(description = "相册图片ID") @PathVariable Long id) {
        PetGallery petGallery = petGalleryService.getById(id);
        if (petGallery != null) {
            return ResponseEntity.ok(petGallery);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 根据ID更新相册图片
     */
    @PutMapping("/petGallery/{id}")
    @Operation(summary = "更新相册图片", description = "根据ID更新相册图片")
    public ResponseEntity<String> updatePetGallery(@Parameter(description = "相册图片ID") @PathVariable Long id, @RequestBody PetGallery petGallery) {
        petGallery.setId(id);
        PetGallery existingGallery = petGalleryService.getById(id);
        if (existingGallery != null) {
            petGallery.setCreatedAt(existingGallery.getCreatedAt());
            boolean updated = petGalleryService.updateById(petGallery);
            if (updated) {
                return ResponseEntity.ok("相册图片更新成功，ID：" + id);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("相册图片更新失败");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("相册图片不存在，ID：" + id);
        }
    }

    /**
     * 根据ID删除相册图片
     */
    @DeleteMapping("/petGallery/{id}")
    @Operation(summary = "删除相册图片", description = "根据ID删除相册图片")
    public ResponseEntity<String> deletePetGallery(@Parameter(description = "相册图片ID") @PathVariable Long id) {
        boolean deleted = petGalleryService.removeById(id);
        if (deleted) {
            return ResponseEntity.ok("相册图片删除成功，ID：" + id);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("相册图片不存在，ID：" + id);
        }
    }

    /**
     * 根据宠物ID获取相册列表
     */
    @GetMapping("/petGallery/pet/{petId}")
    @Operation(summary = "根据宠物ID获取相册", description = "获取指定宠物的所有相册图片")
    public ResponseEntity<List<PetGallery>> getPetGalleryByPetId(@Parameter(description = "宠物ID") @PathVariable Long petId) {
        List<PetGallery> list = petGalleryService.listByPetId(petId);
        return ResponseEntity.ok(list);
    }
}

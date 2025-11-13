package com.tox.tox.pets.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.Pets;
import com.tox.tox.pets.service.IPetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 宠物信息前端控制器
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@RestController
@RequestMapping("/pets")
public class PetsController {

    @Autowired
    private IPetsService petsService;

    /**
     * 添加宠物
     */
    @PostMapping
    public ResponseEntity<Pets> addPet(@RequestBody Pets pet) {
        // 设置创建时间
        pet.setCreatedAt(LocalDateTime.now());
        boolean saved = petsService.save(pet);
        if (saved) {
            return ResponseEntity.status(HttpStatus.CREATED).body(pet);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取宠物列表
     */
    @GetMapping
    public ResponseEntity<List<Pets>> listPets() {
        List<Pets> pets = petsService.list();
        return ResponseEntity.ok(pets);
    }

    /**
     * 分页查询宠物
     */
    @GetMapping("/page")
    public ResponseEntity<Page<Pets>> pagePets(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Pets> page = new Page<>(pageNum, pageSize);
        Page<Pets> resultPage = petsService.page(page);
        return ResponseEntity.ok(resultPage);
    }

    /**
     * 根据ID获取宠物信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pets> getPetById(@PathVariable Long id) {
        Pets pet = petsService.getById(id);
        if (pet != null) {
            return ResponseEntity.ok(pet);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 根据ID更新宠物信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Pets> updatePet(@PathVariable Long id, @RequestBody Pets pet) {
        // 确保ID一致
        pet.setId(id);
        // 不更新创建时间
        Pets existingPet = petsService.getById(id);
        if (existingPet != null) {
            pet.setCreatedAt(existingPet.getCreatedAt());
            boolean updated = petsService.updateById(pet);
            if (updated) {
                return ResponseEntity.ok(pet);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 根据ID删除宠物
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        boolean deleted = petsService.removeById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 根据物种查询宠物
     */
    @GetMapping("/species/{species}")
    public ResponseEntity<List<Pets>> getPetsBySpecies(@PathVariable String species) {
        QueryWrapper<Pets> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("species", species);
        List<Pets> pets = petsService.list(queryWrapper);
        return ResponseEntity.ok(pets);
    }
}
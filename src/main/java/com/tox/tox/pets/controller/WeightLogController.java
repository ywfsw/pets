package com.tox.tox.pets.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.WeightLog;
import com.tox.tox.pets.service.IWeightLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * <p>
 * 宠物体重记录前端控制器
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@RestController
@RequestMapping("/api/weight-logs")
@Tag(name = "体重记录管理", description = "宠物体重记录相关的增删改查接口")
public class WeightLogController {

    @Autowired
    private IWeightLogService weightLogService;

    /**
     * 添加体重记录 - 需要登录
     */
    @SaCheckLogin
    @PostMapping
    @Operation(summary = "添加体重记录", description = "添加一条宠物体重记录")
    public ResponseEntity<String> addWeightLog(@RequestBody WeightLog log) {
        // 设置创建时间
        log.setCreatedAt(OffsetDateTime.now());
        boolean saved = weightLogService.save(log);
        if (saved) {
            return ResponseEntity.status(HttpStatus.CREATED).body("体重记录添加成功，ID：" + log.getId());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("体重记录添加失败");
        }
    }

    /**
     * 获取体重记录列表 - 公开接口
     */
    @GetMapping
    @Operation(summary = "获取体重记录列表", description = "获取所有体重记录列表")
    public ResponseEntity<List<WeightLog>> listWeightLogs() {
        List<WeightLog> logs = weightLogService.list();
        return ResponseEntity.ok(logs);
    }

    /**
     * 分页查询体重记录 - 公开接口
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询体重记录", description = "分页获取体重记录列表")
    public ResponseEntity<Page<WeightLog>> pageWeightLogs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<WeightLog> page = new Page<>(pageNum, pageSize);
        Page<WeightLog> resultPage = weightLogService.page(page);
        return ResponseEntity.ok(resultPage);
    }

    /**
     * 根据ID获取体重记录 - 公开接口
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取体重记录详情", description = "根据ID获取体重记录")
    public ResponseEntity<WeightLog> getWeightLogById(@Parameter(description = "体重记录ID") @PathVariable Long id) {
        WeightLog log = weightLogService.getById(id);
        if (log != null) {
            return ResponseEntity.ok(log);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 根据ID更新体重记录 - 需要登录
     */
    @SaCheckLogin
    @PutMapping("/{id}")
    @Operation(summary = "更新体重记录", description = "根据ID更新体重记录")
    public ResponseEntity<WeightLog> updateWeightLog(@Parameter(description = "体重记录ID") @PathVariable Long id, @RequestBody WeightLog log) {
        // 确保ID一致
        log.setId(id);
        // 不更新创建时间
        WeightLog existingLog = weightLogService.getById(id);
        if (existingLog != null) {
            log.setCreatedAt(existingLog.getCreatedAt());
            boolean updated = weightLogService.updateById(log);
            if (updated) {
                return ResponseEntity.ok(log);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 根据ID删除体重记录 - 需要登录
     */
    @SaCheckLogin
    @DeleteMapping("/{id}")
    @Operation(summary = "删除体重记录", description = "根据ID删除体重记录")
    public ResponseEntity<Void> deleteWeightLog(@Parameter(description = "体重记录ID") @PathVariable Long id) {
        boolean deleted = weightLogService.removeById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

        /**

         * 根据宠物ID获取体重记录历史 - 公开接口

         */

        @GetMapping("/pet/{petId}")
        @Operation(summary = "根据宠物ID获取体重记录", description = "获取指定宠物的所有体重记录")
        public ResponseEntity<List<WeightLog>> getWeightLogsByPetId(@Parameter(description = "宠物ID") @PathVariable Long petId) {

            List<WeightLog> logs = weightLogService.listByPetId(petId);

            return ResponseEntity.ok(logs);

        }



        /**

         * 获取宠物最新体重记录 - 公开接口

         */

        @GetMapping("/pet/{petId}/latest")
        @Operation(summary = "获取宠物最新体重记录", description = "获取指定宠物的最新体重记录")
        public ResponseEntity<WeightLog> getLatestWeightLogByPetId(@Parameter(description = "宠物ID") @PathVariable Long petId) {

            WeightLog log = weightLogService.getLatestByPetId(petId);

            if (log != null) {

                return ResponseEntity.ok(log);

            } else {

                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            }

        }

    }

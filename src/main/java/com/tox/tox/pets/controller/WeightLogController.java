package com.tox.tox.pets.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.WeightLog;
import com.tox.tox.pets.service.IWeightLogService;
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
public class WeightLogController {

    @Autowired
    private IWeightLogService weightLogService;

    /**
     * 添加体重记录
     */
    @PostMapping
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
     * 获取体重记录列表
     */
    @GetMapping
    public ResponseEntity<List<WeightLog>> listWeightLogs() {
        List<WeightLog> logs = weightLogService.list();
        return ResponseEntity.ok(logs);
    }

    /**
     * 分页查询体重记录
     */
    @GetMapping("/page")
    public ResponseEntity<Page<WeightLog>> pageWeightLogs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<WeightLog> page = new Page<>(pageNum, pageSize);
        Page<WeightLog> resultPage = weightLogService.page(page);
        return ResponseEntity.ok(resultPage);
    }

    /**
     * 根据ID获取体重记录
     */
    @GetMapping("/{id}")
    public ResponseEntity<WeightLog> getWeightLogById(@PathVariable Long id) {
        WeightLog log = weightLogService.getById(id);
        if (log != null) {
            return ResponseEntity.ok(log);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 根据ID更新体重记录
     */
    @PutMapping("/{id}")
    public ResponseEntity<WeightLog> updateWeightLog(@PathVariable Long id, @RequestBody WeightLog log) {
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
     * 根据ID删除体重记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeightLog(@PathVariable Long id) {
        boolean deleted = weightLogService.removeById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

        /**

         * 根据宠物ID获取体重记录历史

         */

        @GetMapping("/pet/{petId}")

        public ResponseEntity<List<WeightLog>> getWeightLogsByPetId(@PathVariable Long petId) {

            List<WeightLog> logs = weightLogService.listByPetId(petId);

            return ResponseEntity.ok(logs);

        }

    

        /**

         * 获取宠物最新体重记录

         */

        @GetMapping("/pet/{petId}/latest")

        public ResponseEntity<WeightLog> getLatestWeightLogByPetId(@PathVariable Long petId) {

            WeightLog log = weightLogService.getLatestByPetId(petId);

            if (log != null) {

                return ResponseEntity.ok(log);

            } else {

                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

            }

        }

    }

    
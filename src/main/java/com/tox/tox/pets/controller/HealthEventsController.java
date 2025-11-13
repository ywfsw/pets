package com.tox.tox.pets.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.HealthEvents;
import com.tox.tox.pets.service.IHealthEventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 宠物健康事件前端控制器
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@RestController
@RequestMapping("/health-events")
public class HealthEventsController {

    @Autowired
    private IHealthEventsService healthEventsService;

    /**
     * 添加健康事件
     */
    @PostMapping
    public ResponseEntity<HealthEvents> addHealthEvent(@RequestBody HealthEvents event) {
        // 设置创建时间
        event.setCreatedAt(LocalDateTime.now());
        boolean saved = healthEventsService.save(event);
        if (saved) {
            return ResponseEntity.status(HttpStatus.CREATED).body(event);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取健康事件列表
     */
    @GetMapping
    public ResponseEntity<List<HealthEvents>> listHealthEvents() {
        List<HealthEvents> events = healthEventsService.list();
        return ResponseEntity.ok(events);
    }

    /**
     * 分页查询健康事件
     */
    @GetMapping("/page")
    public ResponseEntity<Page<HealthEvents>> pageHealthEvents(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<HealthEvents> page = new Page<>(pageNum, pageSize);
        Page<HealthEvents> resultPage = healthEventsService.page(page);
        return ResponseEntity.ok(resultPage);
    }

    /**
     * 根据ID获取健康事件
     */
    @GetMapping("/{id}")
    public ResponseEntity<HealthEvents> getHealthEventById(@PathVariable Long id) {
        HealthEvents event = healthEventsService.getById(id);
        if (event != null) {
            return ResponseEntity.ok(event);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 根据ID更新健康事件
     */
    @PutMapping("/{id}")
    public ResponseEntity<HealthEvents> updateHealthEvent(@PathVariable Long id, @RequestBody HealthEvents event) {
        // 确保ID一致
        event.setId(id);
        // 不更新创建时间
        HealthEvents existingEvent = healthEventsService.getById(id);
        if (existingEvent != null) {
            event.setCreatedAt(existingEvent.getCreatedAt());
            boolean updated = healthEventsService.updateById(event);
            if (updated) {
                return ResponseEntity.ok(event);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 根据ID删除健康事件
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHealthEvent(@PathVariable Long id) {
        boolean deleted = healthEventsService.removeById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 根据宠物ID获取健康事件
     */
    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<HealthEvents>> getHealthEventsByPetId(@PathVariable Long petId) {
        QueryWrapper<HealthEvents> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId);
        queryWrapper.orderByDesc("event_date");
        List<HealthEvents> events = healthEventsService.list(queryWrapper);
        return ResponseEntity.ok(events);
    }

    /**
     * 获取即将到期的健康事件（7天内）
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<HealthEvents>> getUpcomingHealthEvents() {
        QueryWrapper<HealthEvents> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("next_due_date");
        queryWrapper.le("next_due_date", java.time.LocalDate.now().plusDays(7));
        queryWrapper.orderByAsc("next_due_date");
        List<HealthEvents> events = healthEventsService.list(queryWrapper);
        return ResponseEntity.ok(events);
    }
}
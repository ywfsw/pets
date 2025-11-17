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
import java.time.OffsetDateTime;
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
@RequestMapping("/api")
public class HealthEventsController {

    @Autowired
    private IHealthEventsService healthEventsService;

    /**
     * 添加健康事件
     */
    @PostMapping("/health-events")
    public ResponseEntity<String> addHealthEvent(@RequestBody HealthEvents event) {
        // 设置创建时间
        event.setCreatedAt(OffsetDateTime.now());
        boolean saved = healthEventsService.save(event);
        if (saved) {
            return ResponseEntity.status(HttpStatus.CREATED).body("健康事件添加成功，ID：" + event.getId());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("健康事件添加失败");
        }
    }

    /**
     * 获取健康事件列表
     */
    @GetMapping("/health-events")
    public ResponseEntity<List<HealthEvents>> listHealthEvents() {
        List<HealthEvents> events = healthEventsService.list();
        return ResponseEntity.ok(events);
    }

    /**
     * 分页查询健康事件
     */
    @GetMapping("/health-events/page")
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
    @GetMapping("/health-events/{id}")
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
    @PutMapping("/health-events/{id}")
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
    @DeleteMapping("/health-events/{id}")
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

        @GetMapping("/health-events/pet/{petId}")

        public ResponseEntity<List<HealthEvents>> getHealthEventsByPetId(@PathVariable Long petId) {

            List<HealthEvents> events = healthEventsService.listByPetId(petId);

            return ResponseEntity.ok(events);

        }

    

        /**

         * 获取即将到期的健康事件（7天内）

         */

        @GetMapping("/health-events/upcoming")

        public ResponseEntity<List<HealthEvents>> getUpcomingHealthEvents() {

            List<HealthEvents> events = healthEventsService.listUpcoming();

            return ResponseEntity.ok(events);

        }

    }

    
package com.tox.tox.pets.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.DictItems;
import com.tox.tox.pets.model.HealthEvents;
import com.tox.tox.pets.model.Pets;
import com.tox.tox.pets.model.dto.HealthEventsDTO;
import com.tox.tox.pets.service.IDictItemsService;
import com.tox.tox.pets.service.IHealthEventsService;
import com.tox.tox.pets.service.IPetsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
@Tag(name = "健康事件管理", description = "宠物健康事件相关的增删改查接口")
public class HealthEventsController {

    @Autowired
    private IHealthEventsService healthEventsService;

    @Autowired
    private IPetsService petsService;

    @Autowired
    private IDictItemsService dictItemsService;

    /**
     * 添加健康事件 - 需要登录
     */
    @SaCheckLogin
    @PostMapping("/health-events")
    @Operation(summary = "添加健康事件", description = "添加一个宠物健康事件")
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
     * 获取健康事件列表 - 公开接口
     */
    @GetMapping("/health-events")
    @Operation(summary = "获取健康事件列表", description = "获取所有健康事件列表")
    public ResponseEntity<List<HealthEvents>> listHealthEvents() {
        List<HealthEvents> events = healthEventsService.list();
        return ResponseEntity.ok(events);
    }

    /**
     * 分页查询健康事件 - 公开接口
     */
    @GetMapping("/health-events/page")
    @Operation(summary = "分页查询健康事件", description = "分页获取健康事件列表")
    public ResponseEntity<Page<HealthEvents>> pageHealthEvents(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<HealthEvents> page = new Page<>(pageNum, pageSize);
        Page<HealthEvents> resultPage = healthEventsService.page(page);
        return ResponseEntity.ok(resultPage);
    }

    /**
     * 根据ID获取健康事件 - 公开接口
     */
    @GetMapping("/health-events/{id}")
    @Operation(summary = "获取健康事件详情", description = "根据ID获取健康事件")
    public ResponseEntity<HealthEvents> getHealthEventById(@Parameter(description = "健康事件ID") @PathVariable Long id) {
        HealthEvents event = healthEventsService.getById(id);
        if (event != null) {
            return ResponseEntity.ok(event);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 根据ID更新健康事件 - 需要登录
     */
    @SaCheckLogin
    @PutMapping("/health-events/{id}")
    @Operation(summary = "更新健康事件", description = "根据ID更新健康事件")
    public ResponseEntity<HealthEvents> updateHealthEvent(@Parameter(description = "健康事件ID") @PathVariable Long id, @RequestBody HealthEvents event) {
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
     * 根据ID删除健康事件 - 需要登录
     */
    @SaCheckLogin
    @DeleteMapping("/health-events/{id}")
    @Operation(summary = "删除健康事件", description = "根据ID删除健康事件")
    public ResponseEntity<Void> deleteHealthEvent(@Parameter(description = "健康事件ID") @PathVariable Long id) {
        boolean deleted = healthEventsService.removeById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

        /**

         * 根据宠物ID获取健康事件 - 公开接口

         */

        @GetMapping("/health-events/pet/{petId}")
        @Operation(summary = "根据宠物ID获取健康事件", description = "获取指定宠物的所有健康事件")
        public ResponseEntity<List<HealthEvents>> getHealthEventsByPetId(@Parameter(description = "宠物ID") @PathVariable Long petId) {

            List<HealthEvents> events = healthEventsService.listByPetId(petId);

            return ResponseEntity.ok(events);

        }



        /**

         * 获取即将到期的健康事件（7天内）- 公开接口

         */

        @GetMapping("/health-events/upcoming")
        @Operation(summary = "获取即将到期的健康事件", description = "获取7天内即将到期的健康事件，包含宠物名称和事件类型标签")
        public ResponseEntity<List<HealthEventsDTO>> getUpcomingHealthEvents() {

            List<HealthEvents> events = healthEventsService.listUpcoming();

            if (events.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            // 批量查询宠物名称，避免 N+1 问题
            Set<Long> petIds = events.stream()
                    .map(HealthEvents::getPetId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            Map<Long, String> petNameMap = new HashMap<>();
            if (!petIds.isEmpty()) {
                List<Pets> petsList = petsService.listByIds(petIds);
                for (Pets pet : petsList) {
                    petNameMap.put(pet.getId(), pet.getName());
                }
            }

            // 批量查询事件类型标签，避免 N+1 问题
            Set<Long> eventTypeIds = events.stream()
                    .map(HealthEvents::getEventTypeId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            Map<Long, String> eventTypeLabelMap = new HashMap<>();
            if (!eventTypeIds.isEmpty()) {
                List<DictItems> typeItems = dictItemsService.listByIds(eventTypeIds);
                for (DictItems item : typeItems) {
                    eventTypeLabelMap.put(item.getId(), item.getItemLabel());
                }
            }

            // 转换为 DTO
            List<HealthEventsDTO> dtos = new ArrayList<>();
            for (HealthEvents event : events) {
                HealthEventsDTO dto = new HealthEventsDTO();
                dto.setId(event.getId());
                dto.setPetId(event.getPetId());
                dto.setEventTypeId(event.getEventTypeId());
                dto.setEventDate(event.getEventDate());
                dto.setNextDueDate(event.getNextDueDate());
                dto.setNotes(event.getNotes());
                dto.setStatus(event.getStatus());
                dto.setCreatedAt(event.getCreatedAt());
                dto.setPetName(petNameMap.getOrDefault(event.getPetId(), "未知宠物"));
                dto.setEventTypeLabel(eventTypeLabelMap.getOrDefault(event.getEventTypeId(), "未知事件"));
                dtos.add(dto);
            }

            return ResponseEntity.ok(dtos);

        }

        /**
         * 标记健康事件为已完成 - 需要登录
         */
        @SaCheckLogin
        @PatchMapping("/health-events/{id}/complete")
        @Operation(summary = "标记事件为已完成", description = "将指定健康事件标记为已完成状态")
        public ResponseEntity<String> completeHealthEvent(@Parameter(description = "健康事件ID") @PathVariable Long id) {
            boolean completed = healthEventsService.completeEvent(id);
            if (completed) {
                return ResponseEntity.ok("事件已标记为完成");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("事件不存在");
            }
        }

        /**
         * 撤销健康事件的已完成状态 - 需要登录
         */
        @SaCheckLogin
        @PatchMapping("/health-events/{id}/uncomplete")
        @Operation(summary = "撤销事件已完成", description = "将已完成的健康事件恢复为待处理状态")
        public ResponseEntity<String> uncompleteHealthEvent(@Parameter(description = "健康事件ID") @PathVariable Long id) {
            boolean uncompleted = healthEventsService.uncompleteEvent(id);
            if (uncompleted) {
                return ResponseEntity.ok("事件已恢复为待处理");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("事件不存在");
            }
        }

    }

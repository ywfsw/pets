package com.tox.tox.pets.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.BathingRecord;
import com.tox.tox.pets.model.dto.BathingStatsDTO;
import com.tox.tox.pets.service.IBathingRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "洗澡美容记录管理", description = "宠物洗澡美容记录的增删改查接口")
public class BathingRecordController {

    @Autowired
    private IBathingRecordService bathingRecordService;

    @SaCheckLogin
    @PostMapping("/bathing-records")
    @Operation(summary = "添加洗澡美容记录")
    public ResponseEntity<String> addBathingRecord(@RequestBody BathingRecord record) {
        record.setCreatedAt(OffsetDateTime.now());
        boolean saved = bathingRecordService.save(record);
        if (saved) {
            return ResponseEntity.status(HttpStatus.CREATED).body("洗澡美容记录添加成功，ID：" + record.getId());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("洗澡美容记录添加失败");
        }
    }

    @GetMapping("/bathing-records/pet/{petId}")
    @Operation(summary = "根据宠物ID获取洗澡美容记录")
    public ResponseEntity<List<BathingRecord>> getBathingRecordsByPetId(
            @Parameter(description = "宠物ID") @PathVariable Long petId) {
        List<BathingRecord> records = bathingRecordService.listByPetId(petId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/bathing-records/stats")
    @Operation(summary = "获取洗澡美容统计数据", description = "按服务类型聚合的统计，支持按宠物筛选和时间范围")
    public ResponseEntity<BathingStatsDTO> getBathingStats(
            @Parameter(description = "宠物ID") @RequestParam(required = false) Long petId,
            @Parameter(description = "统计天数，如30、90，默认30") @RequestParam(defaultValue = "30") Integer days) {
        BathingStatsDTO stats = bathingRecordService.getBathingStats(petId, days);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/bathing-records/page")
    @Operation(summary = "分页查询洗澡美容记录", description = "分页获取洗澡美容记录列表，支持按宠物ID筛选")
    public ResponseEntity<Page<BathingRecord>> pageBathingRecords(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "宠物ID") @RequestParam(required = false) Long petId) {
        Page<BathingRecord> resultPage = bathingRecordService.pageByPetId(pageNum, pageSize, petId);
        return ResponseEntity.ok(resultPage);
    }

    @SaCheckLogin
    @PutMapping("/bathing-records/{id}")
    @Operation(summary = "更新洗澡美容记录")
    public ResponseEntity<BathingRecord> updateBathingRecord(
            @Parameter(description = "洗澡美容记录ID") @PathVariable Long id,
            @RequestBody BathingRecord record) {
        record.setId(id);
        BathingRecord existing = bathingRecordService.getById(id);
        if (existing != null) {
            record.setCreatedAt(existing.getCreatedAt());
            boolean updated = bathingRecordService.updateById(record);
            if (updated) {
                return ResponseEntity.ok(record);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @SaCheckLogin
    @DeleteMapping("/bathing-records/{id}")
    @Operation(summary = "删除洗澡美容记录")
    public ResponseEntity<Void> deleteBathingRecord(
            @Parameter(description = "洗澡美容记录ID") @PathVariable Long id) {
        boolean deleted = bathingRecordService.removeById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

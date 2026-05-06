package com.tox.tox.pets.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.MedicationRecord;
import com.tox.tox.pets.model.dto.MedicationStatsDTO;
import com.tox.tox.pets.service.IMedicationRecordService;
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
@Tag(name = "用药记录管理", description = "宠物用药记录的增删改查接口")
public class MedicationRecordController {

    @Autowired
    private IMedicationRecordService medicationRecordService;

    @SaCheckLogin
    @PostMapping("/medication-records")
    @Operation(summary = "添加用药记录")
    public ResponseEntity<String> addMedicationRecord(@RequestBody MedicationRecord record) {
        record.setCreatedAt(OffsetDateTime.now());
        boolean saved = medicationRecordService.save(record);
        if (saved) {
            return ResponseEntity.status(HttpStatus.CREATED).body("用药记录添加成功，ID：" + record.getId());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("用药记录添加失败");
        }
    }

    @GetMapping("/medication-records/pet/{petId}")
    @Operation(summary = "根据宠物ID获取用药记录")
    public ResponseEntity<List<MedicationRecord>> getMedicationRecordsByPetId(
            @Parameter(description = "宠物ID") @PathVariable Long petId) {
        List<MedicationRecord> records = medicationRecordService.listByPetId(petId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/medication-records/stats")
    @Operation(summary = "获取用药统计数据", description = "按用药类型聚合的统计，支持按宠物筛选和时间范围")
    public ResponseEntity<MedicationStatsDTO> getMedicationStats(
            @Parameter(description = "宠物ID") @RequestParam(required = false) Long petId,
            @Parameter(description = "统计天数，如30、90，默认30") @RequestParam(defaultValue = "30") Integer days) {
        MedicationStatsDTO stats = medicationRecordService.getMedicationStats(petId, days);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/medication-records/page")
    @Operation(summary = "分页查询用药记录", description = "分页获取用药记录列表，支持按宠物ID筛选")
    public ResponseEntity<Page<MedicationRecord>> pageMedicationRecords(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "宠物ID") @RequestParam(required = false) Long petId) {
        Page<MedicationRecord> resultPage = medicationRecordService.pageByPetId(pageNum, pageSize, petId);
        return ResponseEntity.ok(resultPage);
    }

    @SaCheckLogin
    @PutMapping("/medication-records/{id}")
    @Operation(summary = "更新用药记录")
    public ResponseEntity<MedicationRecord> updateMedicationRecord(
            @Parameter(description = "用药记录ID") @PathVariable Long id,
            @RequestBody MedicationRecord record) {
        record.setId(id);
        MedicationRecord existing = medicationRecordService.getById(id);
        if (existing != null) {
            record.setCreatedAt(existing.getCreatedAt());
            boolean updated = medicationRecordService.updateById(record);
            if (updated) {
                return ResponseEntity.ok(record);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @SaCheckLogin
    @DeleteMapping("/medication-records/{id}")
    @Operation(summary = "删除用药记录")
    public ResponseEntity<Void> deleteMedicationRecord(
            @Parameter(description = "用药记录ID") @PathVariable Long id) {
        boolean deleted = medicationRecordService.removeById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

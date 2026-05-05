package com.tox.tox.pets.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.tox.tox.pets.model.FeedingRecord;
import com.tox.tox.pets.service.IFeedingRecordService;
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
@Tag(name = "喂养记录管理", description = "宠物喂养记录的增删改查接口")
public class FeedingRecordController {

    @Autowired
    private IFeedingRecordService feedingRecordService;

    @SaCheckLogin
    @PostMapping("/feeding-records")
    @Operation(summary = "添加喂养记录")
    public ResponseEntity<String> addFeedingRecord(@RequestBody FeedingRecord record) {
        record.setCreatedAt(OffsetDateTime.now());
        boolean saved = feedingRecordService.save(record);
        if (saved) {
            return ResponseEntity.status(HttpStatus.CREATED).body("喂养记录添加成功，ID：" + record.getId());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("喂养记录添加失败");
        }
    }

    @GetMapping("/feeding-records/pet/{petId}")
    @Operation(summary = "根据宠物ID获取喂养记录")
    public ResponseEntity<List<FeedingRecord>> getFeedingRecordsByPetId(
            @Parameter(description = "宠物ID") @PathVariable Long petId) {
        List<FeedingRecord> records = feedingRecordService.listByPetId(petId);
        return ResponseEntity.ok(records);
    }

    @SaCheckLogin
    @PutMapping("/feeding-records/{id}")
    @Operation(summary = "更新喂养记录")
    public ResponseEntity<FeedingRecord> updateFeedingRecord(
            @Parameter(description = "喂养记录ID") @PathVariable Long id,
            @RequestBody FeedingRecord record) {
        record.setId(id);
        FeedingRecord existing = feedingRecordService.getById(id);
        if (existing != null) {
            record.setCreatedAt(existing.getCreatedAt());
            boolean updated = feedingRecordService.updateById(record);
            if (updated) {
                return ResponseEntity.ok(record);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @SaCheckLogin
    @DeleteMapping("/feeding-records/{id}")
    @Operation(summary = "删除喂养记录")
    public ResponseEntity<Void> deleteFeedingRecord(
            @Parameter(description = "喂养记录ID") @PathVariable Long id) {
        boolean deleted = feedingRecordService.removeById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

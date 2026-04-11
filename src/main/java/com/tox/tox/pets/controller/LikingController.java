package com.tox.tox.pets.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import com.tox.tox.pets.model.dto.ErrorResponseDTO;
import com.tox.tox.pets.model.dto.LikeCountDTO;
import com.tox.tox.pets.model.dto.LikeResponseDTO;
import com.tox.tox.pets.service.LikingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞功能的 API Controller
 * 遵循 Rule 3.1 (Production-Ready)
 */
@RestController
@RequestMapping("/api")
@Tag(name = "点赞管理", description = "宠物点赞相关接口")
public class LikingController {

    private final LikingService likingService;

    // (❗) 构造函数注入 (Spring 推荐)
    @Autowired
    public LikingController(LikingService likingService) {
        this.likingService = likingService;
    }

    /**
     * API: 点赞一个宠物 - 公开接口，任何人都可以点赞
     *
     * @param petId 要点赞的宠物 ID (来自路径)
     * @return LikeResponseDTO (告知前端点赞是否成功)
     */
    @PostMapping("/pets/{petId}/like")
    @Operation(summary = "点赞宠物", description = "为指定宠物点赞")
    public ResponseEntity<LikeResponseDTO> likePet(
            @Parameter(description = "宠物ID") @PathVariable Long petId
    ) {
        // 调用 Service 层
        likingService.likePet(petId);
        return ResponseEntity.ok(new LikeResponseDTO(true, "点赞成功"));
    }

    /**
     * API: 获取宠物的总点赞数 - 公开接口
     *
     * @param petId 宠物 ID (来自路径)
     * @return LikeCountDTO (包含总数)
     */
    @GetMapping("/pets/{petId}/likes/count")
    @Operation(summary = "获取宠物点赞数", description = "获取指定宠物的点赞总数")
    public ResponseEntity<LikeCountDTO> getPetLikeCount(@Parameter(description = "宠物ID") @PathVariable Long petId) {

        long count = likingService.getPetLikeCount(petId);

        // (HTTP 200 OK)
        return ResponseEntity.ok(new LikeCountDTO(petId, count));
    }

    /**
     * (❗ 健壮性) Controller 级别的异常处理器
     * (捕获 LikingService 中抛出的 IllegalArgumentException)
     *
     * @param ex 异常
     * @return HTTP 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
        // (HTTP 400 Bad Request)
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(ex.getMessage()));
    }
}

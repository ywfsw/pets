package com.tox.tox.pets.controller;


import com.tox.tox.pets.model.dto.ErrorResponseDTO;
import com.tox.tox.pets.model.dto.LikeCountDTO;
import com.tox.tox.pets.model.dto.LikeResponseDTO;
import com.tox.tox.pets.service.LikingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// (❗) TODO: 当你引入 Spring Security 时, 取消下面这行注释
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.core.userdetails.UserDetails;

/**
 * 点赞功能的 API Controller
 * 遵循 Rule 3.1 (Production-Ready)
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LikingController {

    private final LikingService likingService;

    // (❗) 构造函数注入 (Spring 推荐)
    @Autowired
    public LikingController(LikingService likingService) {
        this.likingService = likingService;
    }

    /**
     * API: 点赞一个宠物
     *
     * @param petId 要点赞的宠物 ID (来自路径)
     * @return LikeResponseDTO (告知前端点赞是否成功)
     */
    @PostMapping("/pets/{petId}/like")
    public ResponseEntity<LikeResponseDTO> likePet(
            @PathVariable Long petId
            // (❗) TODO: 生产环境中, 必须从 Spring Security 获取用户
            // (取消注释下面这行, 并替换掉 STUBBED_USER_ID)
            // @AuthenticationPrincipal UserDetails userDetails 
    ) {

        // 调用 Service 层
        likingService.likePet(petId);
            return ResponseEntity.ok(new LikeResponseDTO(true, "点赞成功"));
    }

    /**
     * API: 获取宠物的总点赞数
     *
     * @param petId 宠物 ID (来自路径)
     * @return LikeCountDTO (包含总数)
     */
    @GetMapping("/pets/{petId}/likes/count")
    public ResponseEntity<LikeCountDTO> getPetLikeCount(@PathVariable Long petId) {
        
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






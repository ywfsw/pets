package com.tox.tox.pets.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查 / 保活接口
 */
@RestController
@Tag(name = "健康检查", description = "系统健康检查接口")
public class HealthController {

    /**
     * (❗ 升级版：增加了 Cache-Control 响应头)
     */
    @GetMapping("/ping")
    @Operation(summary = "健康检查", description = "返回pong，用于服务健康检查")
    public ResponseEntity<String> ping() {

        // (❗) 构造 Cache-Control: "no-store, no-cache, must-revalidate"
        // (这是最强的“禁止缓存”指令)
        CacheControl cacheControl = CacheControl
                .noStore()
                .mustRevalidate();

        // (❗) 返回 200 OK, "pong", 和禁止缓存的 Header
        return ResponseEntity.ok()
                .cacheControl(cacheControl)
                .body("pong");
    }
}
package com.tox.tox.pets.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查 / 保活接口
 */
@RestController
public class HealthController {

    /**
     * (❗ 升级版：增加了 Cache-Control 响应头)
     */
    @GetMapping("/ping")
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
package com.tox.tox.pets.controller;

import com.tox.tox.pets.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 媒体/文件上传接口
 */
@RestController
@RequestMapping("/api")// (Controller) 匹配前端的 /api/media/...
public class MediaController {

    private final CloudinaryService cloudinaryService;

    @Autowired
    public MediaController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * API: 获取 Cloudinary 安全上传签名
     * (❗ 更改为 POST, 接收前端的动态参数)
     */
    @PostMapping("/media/upload-signature")
    public ResponseEntity<Map<String, String>> getUploadSignature(@RequestBody Map<String, Object> widgetParams) {
        // (调用 Service 生成签名，传入前端的动态参数)
        Map<String, String> signature = cloudinaryService.getUploadSignature(widgetParams);
        return ResponseEntity.ok(signature);
    }
}
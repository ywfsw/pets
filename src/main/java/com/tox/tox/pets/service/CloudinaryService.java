package com.tox.tox.pets.service;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Cloudinary 签名生成服务 (后端安全核心)
 */
@Service
public class CloudinaryService {

    private Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    // (初始化方法保持不变)
    @PostConstruct
    public void init() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        this.cloudinary = new Cloudinary(config);
    }

    /**
     * (核心方法) 生成安全的上传签名
     * (❗ 升级: 接收前端传来的动态参数 Map)
     * @param widgetParams 包含 tags, folder 等动态参数
     * @return 包含 signature, timestamp, apiKey, cloudName 的 Map
     */
    public Map<String, String> getUploadSignature(Map<String, Object> widgetParams) {
        if (this.cloudinary == null) {
            throw new IllegalStateException("Cloudinary service is not initialized.");
        }

        // 1. (核心) 准备参数, 并清理空值
        Map<String, Object> params = new HashMap<>(widgetParams);
        params.values().removeIf(Objects::isNull); // 确保参数中没有 null 值

        // (❗ 必须包含 Cloudinary 期望签名的参数)
        // (将 source=uw 作为默认参数)
        params.putIfAbsent("source", "uw");

        // 2. (核心) 添加 timestamp
        long timestamp = System.currentTimeMillis() / 1000L;
        params.put("timestamp", timestamp);

        // 3. 使用 apiSignRequest() 签名
        String signature = this.cloudinary.apiSignRequest(params, this.apiSecret);

        // 4. 组装返回给前端的 DTO
        Map<String, String> response = new HashMap<>();
        response.put("signature", signature);
        response.put("timestamp", String.valueOf(timestamp));
        response.put("api_key", apiKey);
        response.put("cloud_name", cloudName);

        return response;
    }
}
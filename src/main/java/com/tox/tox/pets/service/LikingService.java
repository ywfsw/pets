// (LikingService.java)
package com.tox.tox.pets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 点赞功能 Service, 使用 Redis
 * 遵循 Rule 3.1: 健壮的、生产级的代码
 */
@Service
public class LikingService {

    // (❗) 推荐使用 StringRedisTemplate
    // Key 和 Value 都是 String, 非常适合点赞计数
    private final StringRedisTemplate redisTemplate;

    // (❗) 构造函数注入 (Spring 推荐)
    @Autowired
    public LikingService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

// (在 com.example.pets.service.LikingService 中)
// (❗) 覆盖这个方法
// 遵循 Rule 3.3: 提供完整代码片段

    /**
     * (❗ 简化版) 业务逻辑: 宠物点赞 (无限)
     * (不再需要 userId, 也不再返回 boolean)
     *
     * @param petId 要点赞的宠物 ID
     */
    public void likePet(Long petId) {
        // (后端架构师必备) 必须检查参数
        if (petId == null) {
            throw new IllegalArgumentException("Pet ID 不能为空");
        }

        // --- 核心 Redis 逻辑 ---

        // 1. (❗) (已删除) 防刷 Set (SADD) 逻辑
        // (不再需要检查 userId)

        // 2. (❗) (保留) 计数
        // 我们只保留这一行:
        String likeCountKey = "pet:like:count:" + petId;

        // (INCR) 原子增1
        redisTemplate.opsForValue().increment(likeCountKey);
    }

    /**
     * (业务逻辑) 获取宠物的总点赞数
     *
     * @param petId 宠物 ID
     * @return 总点赞数
     */
    public long getPetLikeCount(Long petId) {
        if (petId == null) return 0;
        
        String likeCountKey = "pet:like:count:" + petId;
        
        // (GET) 获取计数值
        String countStr = redisTemplate.opsForValue().get(likeCountKey);
        
        if (countStr == null) {
            return 0; // 还没有人点赞
        }
        
        try {
            return Long.parseLong(countStr);
        } catch (NumberFormatException e) {
            // (健壮性) 如果数据被污染了 (不是数字), 返回 0
            return 0;
        }
    }

    /**
     * (❗ 新增) 批量获取多个 Pet 的点赞数 (MGET)
     *
     * @param petIds 宠物 ID 列表
     * @return Map<PetID, LikeCount>
     */
    public Map<Long, Long> getPetLikeCounts(List<Long> petIds) {
        if (petIds == null || petIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 1. (MGET) 构造所有的 Key
        List<String> keys = petIds.stream()
                .map(id -> "pet:like:count:" + id)
                .collect(Collectors.toList());

        // 2. (MGET) 一次性从 Redis 获取所有值
        List<String> countStrings = redisTemplate.opsForValue().multiGet(keys);

        // 3. (MGET) 组装回 Map
        Map<Long, Long> countsMap = new HashMap<>();
        if (countStrings == null) {
            return countsMap;
        }

        for (int i = 0; i < petIds.size(); i++) {
            Long petId = petIds.get(i);
            String countStr = countStrings.get(i);

            long count = 0L;
            if (countStr != null) {
                try {
                    count = Long.parseLong(countStr);
                } catch (NumberFormatException e) {
                    // (健壮性) 忽略被污染的脏数据
                }
            }
            countsMap.put(petId, count);
        }

        return countsMap;
    }
}
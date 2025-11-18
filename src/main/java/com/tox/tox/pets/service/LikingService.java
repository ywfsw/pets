// (LikingService.java)
package com.tox.tox.pets.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tox.tox.pets.model.Pets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 点赞功能 Service, 使用 Redis
 * 遵循 Rule 3.1: 健壮的、生产级的代码
 */
@Service
@Slf4j
public class LikingService {

    private final StringRedisTemplate redisTemplate;
    private IPetsService petsService; // 改为非 final
    private final ObjectMapper objectMapper;

    private static final String PETS_LEADERBOARD_KEY = "pets:leaderboard";

    // 构造函数中移除 IPetsService
    @Autowired
    public LikingService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // 使用 Setter 注入
    @Autowired
    public void setPetsService(@Lazy IPetsService petsService) {
        this.petsService = petsService;
    }

    // 用于序列化到 Redis 的内部 DTO
    @Data
    @AllArgsConstructor
    private static class PetLeaderboardMember {
        private Long petId;
        private String name;
        private String profileImageUrl;
    }

    public void likePet(Long petId) {
        if (petId == null) {
            throw new IllegalArgumentException("Pet ID 不能为空");
        }

        String likeCountKey = "pet:like:count:" + petId;
        Long newCount = redisTemplate.opsForValue().increment(likeCountKey);

        if (newCount != null) {
            // 1. 获取宠物详细信息
            Pets pet = petsService.getById(petId);
            if (pet == null) {
                log.warn("点赞的宠物不存在, Pet ID: {}", petId);
                return;
            }

            // 2. 准备要存入 ZSET 的新成员 (JSON 格式)
            PetLeaderboardMember member = new PetLeaderboardMember(pet.getId(), pet.getName(), pet.getProfileImageUrl());
            try {
                String memberJson = objectMapper.writeValueAsString(member);

                // 3. (重要) 移除旧格式的成员 (只包含 petId 的字符串)
                // 这一步是为了数据迁移, 从旧格式过渡到新格式
                // 在稳定运行后, 这行代码可以被移除
                redisTemplate.opsForZSet().remove(PETS_LEADERBOARD_KEY, petId.toString());

                // 4. 将新格式的成员 (JSON) 添加到 ZSET
                redisTemplate.opsForZSet().add(PETS_LEADERBOARD_KEY, memberJson, newCount);

            } catch (JsonProcessingException e) {
                log.error("序列化 PetLeaderboardMember 失败, Pet ID: {}", petId, e);
            }
        }
    }

    public long getPetLikeCount(Long petId) {
        if (petId == null) return 0;
        String likeCountKey = "pet:like:count:" + petId;
        String countStr = redisTemplate.opsForValue().get(likeCountKey);
        if (countStr == null) {
            return 0;
        }
        try {
            return Long.parseLong(countStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Map<Long, Long> getPetLikeCounts(List<Long> petIds) {
        if (petIds == null || petIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> keys = petIds.stream()
                .map(id -> "pet:like:count:" + id)
                .collect(Collectors.toList());

        List<String> countStrings = redisTemplate.opsForValue().multiGet(keys);

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

    public List<ZSetOperations.TypedTuple<String>> getLeaderboard(int topN) {
        Set<ZSetOperations.TypedTuple<String>> range = redisTemplate.opsForZSet()
                .reverseRangeWithScores(PETS_LEADERBOARD_KEY, 0, topN - 1);

        if (range == null) {
            return Collections.emptyList();
        }

        return range.stream().collect(Collectors.toList());
    }
}
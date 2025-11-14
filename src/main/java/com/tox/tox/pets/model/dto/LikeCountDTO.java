package com.tox.tox.pets.model.dto;

import lombok.Data;

/**
 * 点赞计数的响应 DTO
 */
@Data
public class LikeCountDTO {
    private final Long petId;
    private final long likeCount;

    public LikeCountDTO(Long petId, long likeCount) {
        this.petId = petId;
        this.likeCount = likeCount;
    }
}
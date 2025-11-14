package com.tox.tox.pets.model.dto;

import lombok.Data;

/**
 * 点赞操作的响应 DTO
 */
@Data
public class LikeResponseDTO {
    private final boolean success; // (true=新点赞, false=已点过)
    private final String message;

    public LikeResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
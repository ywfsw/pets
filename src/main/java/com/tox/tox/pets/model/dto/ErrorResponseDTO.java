package com.tox.tox.pets.model.dto;

import lombok.Data;

/**
 * 错误响应 DTO
 */
@Data
public class ErrorResponseDTO {
    private final String error;

    public ErrorResponseDTO(String error) {
        this.error = error;
    }
}
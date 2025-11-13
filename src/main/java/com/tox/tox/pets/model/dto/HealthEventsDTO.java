package com.tox.tox.pets.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * 健康事件DTO（带中文标签）
 */
@Getter
@Setter
@ToString
public class HealthEventsDTO {
    /**
     * 健康事件ID (主键)
     */
    private Long id;

    /**
     * 关联的宠物ID
     */
    private Long petId;

    /**
     * 事件类型ID
     */
    private Long eventTypeId;
    
    /**
     * 事件类型中文标签
     */
    private String eventTypeLabel;

    /**
     * 事件实际发生日期
     */
    private LocalDate eventDate;

    /**
     * 下次应办日期
     */
    private LocalDate nextDueDate;

    /**
     * 备注
     */
    private String notes;

    /**
     * 记录创建时间
     */
    private OffsetDateTime createdAt;
}
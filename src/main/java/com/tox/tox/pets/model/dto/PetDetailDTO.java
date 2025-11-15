package com.tox.tox.pets.model.dto;

import com.tox.tox.pets.model.HealthEvents;
import com.tox.tox.pets.model.WeightLog;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * 宠物详细信息DTO
 */
@Getter
@Setter
@ToString
public class PetDetailDTO {
    /**
     * 宠物唯一ID
     */
    private Long id;
    
    /**
     * 物种ID
     */
    private Long speciesId;
    
    /**
     * 物种中文标签
     */
    private String speciesLabel;
    
    /**
     * 品种ID
     */
    private Long breedId;
    
    /**
     * 品种中文标签
     */
    private String breedLabel;
    
    /**
     * 宠物的名字
     */
    private String name;
    
    /**
     * 生日
     */
    private LocalDate birthday;
    
    /**
     * 记录创建时间
     */
    private OffsetDateTime createdAt;
    
    /**
     * 体重记录列表
     */
    private List<WeightLog> weightLogs;
    
    /**
     * 健康事件列表（带中文标签）
     */
    private List<HealthEventsDTO> healthEvents;



    private String profileImageUrl;


    private String profileImagePublicId;
}
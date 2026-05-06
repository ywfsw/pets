package com.tox.tox.pets.model.dto;

import com.tox.tox.pets.model.BathingRecord;
import com.tox.tox.pets.model.FeedingRecord;
import com.tox.tox.pets.model.WeightLog;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * 宠物详细信息DTO
 */
@Getter
@Setter
@ToString
public class PetDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 性别：male-公, female-母, null-未知
     */
    private String gender;

    /**
     * 备注/简介
     */
    private String notes;
    
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

        private String avatarUrl;

        private String avatarId;

    /**
     * 喂养记录列表
     */
    private List<FeedingRecord> feedingRecords;

    /**
     * 洗澡美容记录列表
     */
    private List<BathingRecord> bathingRecords;

    /**
     * 点赞数
     */
    private long likeCount;
}
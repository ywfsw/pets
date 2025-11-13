package com.tox.tox.pets.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 宠物健康事件表 (核心提醒功能)
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Getter
@Setter
@ToString
@TableName("health_events")
public class HealthEvents implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 健康事件ID (主键)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的宠物ID (外键, 关联 pets.id)
     */
    private Long petId;

    /**
     * 事件类型 (关联 health_event_type 枚举)
     */
    private String eventType;

    /**
     * 事件实际发生日期
     */
    private LocalDate eventDate;

    /**
     * 下次应办日期 (用于生成提醒)
     */
    private LocalDate nextDueDate;

    /**
     * 备注 (例如: 疫苗品牌, 医院, 医生等)
     */
    private String notes;

    /**
     * 记录创建时间
     */
    private LocalDateTime createdAt;
}

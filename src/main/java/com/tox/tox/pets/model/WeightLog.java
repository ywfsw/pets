package com.tox.tox.pets.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 宠物体重记录表 (一对多)
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Getter
@Setter
@ToString
@TableName("weight_log")
public class WeightLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 体重记录ID (主键)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的宠物ID (外键, 关联 pets.id)
     */
    private Long petId;

    /**
     * 体重 (单位: 千克, 例如: 5.20)
     */
    private BigDecimal weightKg;

    /**
     * 记录日期 (称重日期)
     */
    private LocalDate logDate;

    /**
     * 记录创建时间
     */
    private LocalDateTime createdAt;
}

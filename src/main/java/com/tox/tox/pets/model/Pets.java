package com.tox.tox.pets.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * <p>
 * 宠物档案主表
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Getter
@Setter
@ToString
@TableName("pets")
public class Pets implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 宠物唯一ID (主键)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 物种ID (外键, 关联 dict_items.id, e.g., '猫')
     */
    private Long speciesId;

    /**
     * 品种ID (外键, 关联 dict_items.id, e.g., '布偶猫')
     */
    private Long breedId;

    /**
     * 宠物的名字
     */
    private String name;

    /**
     * 生日 (用于计算年龄)
     */
    private LocalDate birthday;

    /**
     * 记录创建时间
     */
    private OffsetDateTime createdAt;
}

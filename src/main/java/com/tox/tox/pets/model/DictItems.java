package com.tox.tox.pets.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * <p>
 * 通用字典项表
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Getter
@Setter
@ToString
@TableName("dict_items")
public class DictItems implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典项ID (主键)
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字典编码 (用于分组, 例如: PET_SPECIES, PET_BREED)
     */
    private String dictCode;

    /**
     * 字典项值 (e.g., 'cat', 'ragdoll')
     */
    private String itemValue;

    /**
     * 字典项标签 (e.g., '猫', '布偶猫')
     */
    private String itemLabel;

    /**
     * 父级ID (用于层级关系, 关联 dict_items.id)
     */
    private Long parentId;

    /**
     * 排序字段
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String notes;

    /**
     * 记录创建时间
     */
    private OffsetDateTime createdAt;
}

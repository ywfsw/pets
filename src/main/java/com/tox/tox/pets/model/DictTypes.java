package com.tox.tox.pets.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * <p>
 * 字典类型主表 (用于管理所有字典分组)
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Getter
@Setter
@ToString
@TableName("dict_types")
public class DictTypes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典编码 (主键, e.g., 'PET_SPECIES')
     */
    @TableId("dict_code")
    private String dictCode;

    /**
     * 字典名称 (e.g., '宠物物种')
     */
    private String dictName;

    /**
     * 备注
     */
    private String notes;

    /**
     * 记录创建时间
     */
    private OffsetDateTime createdAt;

    /**
     * 父级字典编码 (用于管理后台的类型分组)
     */
    private String parentCode;
}

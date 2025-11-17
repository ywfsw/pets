package com.tox.tox.pets.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 字典项查找 DTO (用于下拉选择器)
 * (遵循 Rule 3.2.2: 默认只使用 @Data)
 */
@Data
public class DictItemLookupDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典项的主键 ID (e.g., "cat" 的 ID, 10)
     * (前端 Select 组件绑定的 value)
     */
    private Long id;

    /**
     * 字典项标签 (e.g., "猫")
     * (前端 Select 组件显示的 label)
     */
    private String label;
    
    // (可选) 如果你的前端选择器需要构建树形, 可以保留 parentId
    // private Long parentId;
}
package com.tox.tox.pets.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.OffsetDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 存储宠物的相册图片 (多张，与 pets 表一对多关联)
 * </p>
 *
 * @author tox
 * @since 2025-11-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pet_gallery")
public class PetGallery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 宠物ID (关联 pets.id)
     */
    @TableField("pet_id")
    private Long petId;

    /**
     * 图片URL
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * Cloudinary 的 Public ID
     */
    @TableField("public_id")
    private String publicId;

    /**
     * 图片描述或备注信息
     */
    @TableField("description")
    private String description;

    /**
     * 图片在相册中的显示顺序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private OffsetDateTime createdAt;


}
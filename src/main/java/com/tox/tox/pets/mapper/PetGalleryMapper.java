package com.tox.tox.pets.mapper;

import com.tox.tox.pets.model.PetGallery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 存储宠物的相册图片 (多张，与 pets 表一对多关联) Mapper 接口
 * </p>
 *
 * @author tox
 * @since 2025-11-15
 */
public interface PetGalleryMapper extends BaseMapper<PetGallery> {

}
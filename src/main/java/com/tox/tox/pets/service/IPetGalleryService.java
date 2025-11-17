package com.tox.tox.pets.service;

import com.tox.tox.pets.model.PetGallery;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 存储宠物的相册图片 (多张，与 pets 表一对多关联) 服务类
 * </p>
 *
 * @author tox
 * @since 2025-11-15
 */
public interface IPetGalleryService extends IService<PetGallery> {

    List<PetGallery> listByPetId(Long petId);
}
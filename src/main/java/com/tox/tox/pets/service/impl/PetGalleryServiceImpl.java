package com.tox.tox.pets.service.impl;

import com.tox.tox.pets.model.PetGallery;
import com.tox.tox.pets.mapper.PetGalleryMapper;
import com.tox.tox.pets.service.IPetGalleryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 存储宠物的相册图片 (多张，与 pets 表一对多关联) 服务实现类
 * </p>
 *
 * @author tox
 * @since 2025-11-15
 */
@Service
public class PetGalleryServiceImpl extends ServiceImpl<PetGalleryMapper, PetGallery> implements IPetGalleryService {

}
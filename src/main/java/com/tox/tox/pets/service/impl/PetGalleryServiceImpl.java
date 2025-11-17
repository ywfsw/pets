package com.tox.tox.pets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tox.tox.pets.model.PetGallery;
import com.tox.tox.pets.mapper.PetGalleryMapper;
import com.tox.tox.pets.service.IPetGalleryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

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

    @Override
    @Cacheable(value = "pet_gallery_by_pet", key = "#petId")
    public List<PetGallery> listByPetId(Long petId) {
        QueryWrapper<PetGallery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId);
        return this.list(queryWrapper);
    }

    @Override
    @CacheEvict(value = "pet_gallery_by_pet", allEntries = true)
    public boolean save(PetGallery entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = "pet_gallery_by_pet", allEntries = true)
    public boolean updateById(PetGallery entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = "pet_gallery_by_pet", allEntries = true)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
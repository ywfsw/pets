package com.tox.tox.pets.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.Pets;
import com.tox.tox.pets.mapper.PetsMapper;
import com.tox.tox.pets.model.dto.PetPageDTO;
import com.tox.tox.pets.service.IPetsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tox.tox.pets.service.LikingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Service
public class PetsServiceImpl extends ServiceImpl<PetsMapper, Pets> implements IPetsService {
    @Autowired
    private LikingService likingService;
    // (❗) 'baseMapper' (PetMapper) 已经由 ServiceImpl<...> 自动注入了

    /**
     * (❗ 核心实现)
     */
    @Override
    public IPage<PetPageDTO> findPetsWithLikes(int pageNum, int pageSize) {

        // 1. (DB) 创建 MP 分页对象
        IPage<Pets> petPageConfig = new Page<>(pageNum, pageSize);

        // 2. (DB) (❗) 执行 MP 分页查询
        // (this.page() 是 ServiceImpl 提供的)
        IPage<Pets> petPage = this.page(petPageConfig);

        List<Pets> pets = petPage.getRecords();

        // (如果为空, 提前返回一个空的 DTO 分页)
        if (pets.isEmpty()) {
            return new Page<>(pageNum, pageSize, petPage.getTotal());
        }

        // 3. (Redis) 准备批量查询
        List<Long> petIds = pets.stream()
                .map(Pets::getId)
                .collect(Collectors.toList());

        // 4. (Redis) (❗) 一次性 MGET (LikingService 保持不变)
        Map<Long, Long> likeCountsMap = likingService.getPetLikeCounts(petIds);

        // 5. (Java) 组装 DTO 列表
        List<PetPageDTO> dtos = pets.stream().map(pet -> {
            PetPageDTO dto = new PetPageDTO(pet); // (使用我们上次定义的 PetPageDTO)
            long likeCount = likeCountsMap.getOrDefault(pet.getId(), 0L);
            dto.setLikeCount(likeCount);
            return dto;
        }).collect(Collectors.toList());

        // 6. (Java) (❗) 创建 DTO 分页结果
        // (重用 'petPage' 的分页元数据, 但用 'dtos' 替换 'records')
        IPage<PetPageDTO> dtoPage = new Page<>(petPage.getCurrent(), petPage.getSize(), petPage.getTotal());
        dtoPage.setRecords(dtos);

        return dtoPage;
    }
}

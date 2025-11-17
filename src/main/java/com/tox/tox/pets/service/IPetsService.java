package com.tox.tox.pets.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tox.tox.pets.model.Pets;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tox.tox.pets.model.dto.PetDetailDTO;
import com.tox.tox.pets.model.dto.PetLeaderboardDTO;
import com.tox.tox.pets.model.dto.PetPageDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
public interface IPetsService extends IService<Pets> {
    /**
     * (❗ 核心业务) 分页获取宠物, 并聚合点赞数
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return DTO 的分页
     */
    IPage<PetPageDTO> findPetsWithLikes(int pageNum, int pageSize);

    /**
     * (❗ 新增) 获取排行榜
     * @param topN
     * @return
     */
    List<PetLeaderboardDTO> getLeaderboard(int topN);

    PetDetailDTO getPetDetailById(Long id);

    List<Pets> getPetsBySpecies(String species);
}

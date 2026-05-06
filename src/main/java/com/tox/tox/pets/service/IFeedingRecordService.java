package com.tox.tox.pets.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tox.tox.pets.model.FeedingRecord;
import com.tox.tox.pets.model.dto.FeedingStatsDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IFeedingRecordService extends IService<FeedingRecord> {
    List<FeedingRecord> listByPetId(Long petId);
    Page<FeedingRecord> pageByPetId(Integer pageNum, Integer pageSize, Long petId);
    FeedingStatsDTO getFeedingStats(Long petId, Integer days);
}

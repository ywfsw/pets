package com.tox.tox.pets.service;

import com.tox.tox.pets.model.FeedingRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IFeedingRecordService extends IService<FeedingRecord> {
    List<FeedingRecord> listByPetId(Long petId);
}

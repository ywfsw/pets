package com.tox.tox.pets.service;

import com.tox.tox.pets.model.WeightLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 宠物体重记录表 (一对多) 服务类
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
public interface IWeightLogService extends IService<WeightLog> {

    List<WeightLog> listByPetId(Long petId);

    WeightLog getLatestByPetId(Long petId);
}

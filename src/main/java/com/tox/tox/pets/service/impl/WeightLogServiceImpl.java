package com.tox.tox.pets.service.impl;

import com.tox.tox.pets.model.WeightLog;
import com.tox.tox.pets.mapper.WeightLogMapper;
import com.tox.tox.pets.service.IWeightLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 宠物体重记录表 (一对多) 服务实现类
 * </p>
 *
 * @author tox
 * @since 2025-11-13
 */
@Service
public class WeightLogServiceImpl extends ServiceImpl<WeightLogMapper, WeightLog> implements IWeightLogService {

}

package com.tox.tox.pets.service.impl;

import com.tox.tox.pets.model.Pets;
import com.tox.tox.pets.mapper.PetsMapper;
import com.tox.tox.pets.service.IPetsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}

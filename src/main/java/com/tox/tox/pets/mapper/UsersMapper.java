package com.tox.tox.pets.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tox.tox.pets.model.Users;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author tox
 */
@Mapper
public interface UsersMapper extends BaseMapper<Users> {
}

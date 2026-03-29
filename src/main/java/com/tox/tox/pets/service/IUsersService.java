package com.tox.tox.pets.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tox.tox.pets.model.Users;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author tox
 */
public interface IUsersService extends IService<Users> {

    /**
     * 注册用户
     */
    Users register(String username, String password);

    /**
     * 登录验证
     */
    String login(String username, String password);
}

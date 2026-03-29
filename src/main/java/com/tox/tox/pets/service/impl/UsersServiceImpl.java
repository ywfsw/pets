package com.tox.tox.pets.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tox.tox.pets.mapper.UsersMapper;
import com.tox.tox.pets.model.Users;
import com.tox.tox.pets.service.IUsersService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author tox
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {

    @Override
    public Users register(String username, String password) {
        // 检查用户名是否已存在
        long count = this.count(new QueryWrapper<Users>().eq("username", username));
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }

        Users user = new Users();
        user.setUsername(username);
        // 使用 BCrypt 加密密码
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        user.setPassword(hashedPassword);
        user.setRole("USER"); // 默认角色为普通用户
        user.setCreatedAt(OffsetDateTime.now());

        this.save(user);
        return user;
    }

    @Override
    public String login(String username, String password) {
        Users user = this.getOne(new QueryWrapper<Users>().eq("username", username));
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 登录，生成 token
        StpUtil.login(user.getId());
        return StpUtil.getTokenValue();
    }
}

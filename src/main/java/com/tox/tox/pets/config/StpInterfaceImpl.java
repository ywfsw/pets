package com.tox.tox.pets.config;

import cn.dev33.satoken.stp.StpInterface;
import com.tox.tox.pets.model.Users;
import com.tox.tox.pets.service.IUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限验证接口扩展
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private IUsersService usersService;

    /**
     * 返回一个账号所拥有的权限码集合 
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 本系统未细分权限，直接返回空或全部
        return new ArrayList<>();
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();
        long userId = Long.parseLong(loginId.toString());
        Users user = usersService.getById(userId);
        if (user != null && user.getRole() != null) {
            list.add(user.getRole());
        }
        return list;
    }
}

package com.tox.tox.pets.config;

import com.tox.tox.pets.model.Users;
import com.tox.tox.pets.service.IUsersService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

/**
 * 初始化管理员账号
 * 默认管理员: admin / admin123
 */
@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private IUsersService usersService;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已存在管理员
        long adminCount = usersService.count(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Users>()
                .eq("role", "ADMIN")
        );

        if (adminCount == 0) {
            Users admin = new Users();
            admin.setUsername("admin");
            // 密码: admin123
            admin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));
            admin.setRole("ADMIN");
            admin.setCreatedAt(OffsetDateTime.now());
            usersService.save(admin);
            System.out.println("✅ 默认管理员账号已创建: admin / admin123");
        }
    }
}

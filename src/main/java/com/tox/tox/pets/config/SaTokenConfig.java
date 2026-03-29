package com.tox.tox.pets.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 权限拦截配置
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器
        registry.addInterceptor(new SaInterceptor(handler -> {
            
            // 后台管理接口（以 /api/admin/ 为前缀的请求），需要 ADMIN 角色才能访问
            SaRouter.match("/api/admin/**", r -> {
                StpUtil.checkLogin();
                StpUtil.checkRole("ADMIN");
            });

            // 示例：可以配置其他需要登录但不需要特定角色的拦截
            // SaRouter.match("/api/**")
            //        .notMatch("/api/auth/login", "/api/auth/register", "/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
            //        .check(r -> StpUtil.checkLogin());
            
        })).addPathPatterns("/**");
    }
}

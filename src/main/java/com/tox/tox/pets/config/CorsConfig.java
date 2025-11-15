package com.tox.tox.pets.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域资源共享 (CORS) 配置
 * (允许前端 Dev Server 访问后端 API)
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 仅对 /api/ 路径下的所有端点生效
                
                // (❗) 生产中必须严格限制
                // (开发环境: 允许 Vite 默认端口)
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173") 
                
                // 允许的方法: GET, POST, PUT, DELETE, OPTION
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") 
                
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true); // 允许发送 Cookies (认证相关)
    }
}
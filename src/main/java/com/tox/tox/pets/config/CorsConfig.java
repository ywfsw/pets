package com.tox.tox.pets.config; // (替换成你项目的 config 包路径)

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域资源共享 (CORS) 配置
 * (允许前端 Dev Server 和线上 Render 域名访问后端 API)
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // (❗ 你的前端线上域名)
    private static final String FRONTEND_RENDER_DOMAIN = "https://pets-web-irnc.onrender.com";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")

                // (❗ 核心修复：添加线上域名)
                .allowedOrigins(
                        "http://localhost:5173",        // 本地开发端口
                        "http://127.0.0.1:5173",        // 本地开发端口
                        FRONTEND_RENDER_DOMAIN          // 线上域名
                )

                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
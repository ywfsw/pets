package com.tox.tox.pets.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 数据库保活定时任务
 * 用于防止 Neon 免费套餐在闲置 5 分钟后自动休眠
 */
@Slf4j
@Component
public class DatabaseKeepAliveScheduler {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 每 3 分钟执行一次数据库查询，防止 Neon 休眠
     * Neon 免费套餐闲置 5 分钟后会自动休眠
     */
    @Scheduled(fixedRate = 3 * 60 * 1000)
    public void keepDatabaseAlive() {
        try {
            // 执行一个轻量级查询来保持数据库连接活跃
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            log.info("数据库保活任务执行成功");
        } catch (Exception e) {
            log.error("数据库保活任务执行失败: {}", e.getMessage());
        }
    }
}

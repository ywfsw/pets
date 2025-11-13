package com.tox.tox.pets.controller; // (替换成你的包名)

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查 / 保活接口
 */
@RestController
@CrossOrigin(origins = "*")
public class HealthController {

    @GetMapping("/ping")
    public String ping() {
        return "pong"; 
    }
}
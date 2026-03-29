package com.tox.tox.pets.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.annotation.SaCheckLogin;
import com.tox.tox.pets.model.Users;
import com.tox.tox.pets.model.dto.AuthRequestDTO;
import com.tox.tox.pets.service.IUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private IUsersService usersService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        try {
            String token = usersService.login(request.getUsername(), request.getPassword());
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("tokenName", StpUtil.getTokenName());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequestDTO request) {
        try {
            Users user = usersService.register(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(Map.of("message", "注册成功", "userId", user.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @SaCheckLogin
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo() {
        long userId = StpUtil.getLoginIdAsLong();
        Users user = usersService.getById(userId);
        if (user != null) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("role", user.getRole());
            return ResponseEntity.ok(userInfo);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        StpUtil.logout();
        return ResponseEntity.ok(Map.of("message", "已登出"));
    }
}

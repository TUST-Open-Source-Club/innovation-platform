package com.abajin.innovation.controller;

import com.abajin.innovation.common.Result;
import com.abajin.innovation.converter.LoginUserDTOConverter;
import com.abajin.innovation.dto.LoginDTO;
import com.abajin.innovation.dto.LoginUserDTO;
import com.abajin.innovation.dto.RegisterDTO;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.service.CasService;
import com.abajin.innovation.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private CasService casService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            String token = userService.login(loginDTO);
            User user = userService.getUserByUsername(loginDTO.getUsername());
            LoginUserDTO userDTO = LoginUserDTOConverter.convert(user);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", userDTO);

            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            User user = userService.register(registerDTO);
            return Result.success("注册成功", user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户主动退出登录
     * 将当前token加入黑名单，使其立即失效
     */
    @PostMapping("/logout")
    public Result<String> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                casService.logout(token);
                return Result.success("退出登录成功");
            }
            return Result.error("无效的认证信息");
        } catch (Exception e) {
            return Result.error("退出登录失败: " + e.getMessage());
        }
    }
}

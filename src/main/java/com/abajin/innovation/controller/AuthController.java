package com.abajin.innovation.controller;

import com.abajin.innovation.common.Result;
import com.abajin.innovation.dto.LoginDTO;
import com.abajin.innovation.dto.LoginUserDTO;
import com.abajin.innovation.dto.RegisterDTO;
import com.abajin.innovation.entity.User;
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

    /**
     * 将 User 转换为 LoginUserDTO
     */
    private LoginUserDTO convertToLoginUserDTO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserDTO dto = new LoginUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setCollegeId(user.getCollegeId());
        dto.setCollegeName(user.getCollegeName());
        dto.setStatus(user.getStatus());
        dto.setCreateTime(user.getCreateTime());
        dto.setUpdateTime(user.getUpdateTime());
        return dto;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            String token = userService.login(loginDTO);
            User user = userService.getUserByUsername(loginDTO.getUsername());
            LoginUserDTO userDTO = convertToLoginUserDTO(user);

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
}

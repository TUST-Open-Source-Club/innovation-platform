package com.abajin.innovation.controller;

import com.abajin.innovation.common.Result;
import com.abajin.innovation.dto.LoginUserDTO;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 当前用户相关接口（如修改密码、获取当前用户信息）
 */
@RestController
@RequestMapping("/users")
public class UserController {

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

    /**
     * 获取当前登录用户信息
     * GET /api/users/me
     * 用于页面刷新后恢复用户信息和角色
     */
    @GetMapping("/me")
    public Result<LoginUserDTO> getCurrentUser(@RequestAttribute("userId") Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success(convertToLoginUserDTO(user));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改当前用户密码
     * PUT /api/users/me/password
     * 请求体：{ "oldPassword": "原密码", "newPassword": "新密码" }
     */
    @PutMapping("/me/password")
    public Result<Void> changePassword(
            @RequestBody Map<String, String> body,
            @RequestAttribute("userId") Long userId) {
        try {
            String oldPassword = body.get("oldPassword");
            String newPassword = body.get("newPassword");
            userService.changePassword(userId, oldPassword, newPassword);
            return Result.success("密码修改成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

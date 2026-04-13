package com.abajin.innovation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 完善资料DTO
 */
@Data
public class CompleteProfileDTO {
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 学院ID
     */
    @NotNull(message = "学院不能为空")
    private Long collegeId;


    // 注意：角色由统一身份认证或注册时确定，不允讵用户自行修改
    // private String role;
}

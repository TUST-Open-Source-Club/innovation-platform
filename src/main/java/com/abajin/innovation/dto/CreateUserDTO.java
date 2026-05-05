package com.abajin.innovation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 管理员创建用户DTO
 */
@Data
public class CreateUserDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}$", 
             message = "密码至少6位，包含字母和数字")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "角色不能为空")
    private String role; // STUDENT, TEACHER, STUDENT_ADMIN, COLLEGE_ADMIN, SCHOOL_ADMIN

    private Long collegeId; // 所属学院ID

    private Integer status; // 状态：0-禁用，1-启用，默认启用
}

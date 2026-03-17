package com.abajin.innovation.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 登录返回的用户信息DTO
 */
@Data
public class LoginUserDTO {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String role;
    private Long collegeId;
    private String collegeName;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

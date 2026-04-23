package com.abajin.innovation.controller;

import com.abajin.innovation.common.Result;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.mapper.UserMapper;
import com.abajin.innovation.service.CasService;
import com.abajin.innovation.util.JwtUtil;
import com.abajin.innovation.util.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证控制器集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CasService casService;

    @MockitoBean
    private RedisUtil redisUtil;

    @BeforeEach
    void setUp() {
        // 清理测试用户
        User existingUser = userMapper.selectByUsername("testlogout");
        if (existingUser != null) {
            userMapper.deleteById(existingUser.getId());
        }
        // 重置mock并设置默认行为
        reset(redisUtil);
        when(redisUtil.setNx(anyString(), anyString(), anyLong(), any())).thenReturn(true);
        when(redisUtil.exist(anyString())).thenReturn(false);
    }

    @Test
    @DisplayName("测试普通用户退出登录成功")
    void testLogoutSuccess() throws Exception {
        // 创建测试用户
        User user = new User();
        user.setUsername("testlogout");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRealName("测试退出");
        user.setRole("STUDENT");
        user.setAuthType("LOCAL");
        user.setStatus(1);
        user.setIsProfileComplete(1);
        user.setCreateTime(java.time.LocalDateTime.now());
        user.setUpdateTime(java.time.LocalDateTime.now());
        userMapper.insert(user);

        // 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 调用退出登录接口
        MvcResult result = mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Result<?> response = objectMapper.readValue(content, Result.class);

        assertEquals(200, response.getCode());

        // 验证blacklistToken被调用（通过redisUtil.setNx判断）
        verify(redisUtil, atLeastOnce()).setNx(contains("cas:token:blacklist"), eq("1"), anyLong(), any());
    }

    @Test
    @DisplayName("测试未携带token调用退出登录返回403")
    void testLogoutWithoutToken() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("测试退出登录后token无法访问受保护接口")
    void testLogoutTokenBlacklisted() throws Exception {
        // 创建测试用户
        User user = new User();
        user.setUsername("testlogout2");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRealName("测试退出2");
        user.setRole("STUDENT");
        user.setAuthType("LOCAL");
        user.setStatus(1);
        user.setIsProfileComplete(1);
        user.setCreateTime(java.time.LocalDateTime.now());
        user.setUpdateTime(java.time.LocalDateTime.now());
        userMapper.insert(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 先验证可以正常访问
        mockMvc.perform(get("/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 退出登录
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 模拟token已被加入黑名单
        when(redisUtil.exist(anyString())).thenReturn(true);

        // 再次访问应返回403（token在黑名单中）
        mockMvc.perform(get("/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}

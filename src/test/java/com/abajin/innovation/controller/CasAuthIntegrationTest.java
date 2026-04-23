package com.abajin.innovation.controller;

import com.abajin.innovation.common.Constants;
import com.abajin.innovation.common.Result;
import com.abajin.innovation.dto.CasLoginResponse;
import com.abajin.innovation.dto.CasMergeRequest;
import com.abajin.innovation.dto.CompleteProfileDTO;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.mapper.UserMapper;
import com.abajin.innovation.service.CasService;
import com.abajin.innovation.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CAS认证集成测试（使用Mock模式）
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CasAuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CasService casService;

    @MockitoBean
    private com.abajin.innovation.util.RedisUtil redisUtil;

    private static final String MOCK_TICKET = "MOCK-2021001-ZhangSan";
    private static final String MOCK_UID = "2021001";
    private static final String MOCK_NAME = "ZhangSan";

    @BeforeEach
    void setUp() {
        // 清理测试数据
        User existingUser = userMapper.selectByCasUid(MOCK_UID);
        if (existingUser != null) {
            userMapper.deleteById(existingUser.getId());
        }
        User existingByName = userMapper.selectByUsername(MOCK_UID);
        if (existingByName != null) {
            userMapper.deleteById(existingByName.getId());
        }
    }

    @Test
    @DisplayName("测试获取CAS状态 - Mock模式已启用")
    void testGetCasStatus() throws Exception {
        MvcResult result = mockMvc.perform(get("/auth/cas/status"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Result<?> response = objectMapper.readValue(content, Result.class);
        
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        
        // 验证返回的状态包含enabled和mockMode
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> data = (java.util.Map<String, Object>) response.getData();
        assertEquals(Boolean.TRUE, data.get("enabled"));
        assertEquals(Boolean.TRUE, data.get("mockMode"));
    }

    @Test
    @DisplayName("测试Mock模式验证ticket - 新用户登录")
    void testValidateMockTicketNewUser() throws Exception {
        // 确保用户不存在
        assertNull(userMapper.selectByCasUid(MOCK_UID));

        MvcResult result = mockMvc.perform(get("/auth/cas/validate")
                .param("ticket", MOCK_TICKET))
                .andExpect(status().isFound()) // 302 重定向
                .andReturn();

        // 验证重定向到完善资料页面
        String redirectUrl = result.getResponse().getRedirectedUrl();
        assertNotNull(redirectUrl);
        assertTrue(redirectUrl.contains("/complete-profile"));
        assertTrue(redirectUrl.contains("token="));

        // 验证用户已创建到数据库
        User dbUser = userMapper.selectByCasUid(MOCK_UID);
        assertNotNull(dbUser);
        assertEquals(MOCK_NAME, dbUser.getRealName());
        assertEquals(Constants.AUTH_TYPE_CAS, dbUser.getAuthType());
        assertEquals(Integer.valueOf(0), dbUser.getIsProfileComplete());
    }

    @Test
    @DisplayName("测试Mock模式验证ticket - 已存在的CAS用户直接登录")
    void testValidateMockTicketExistingUser() throws Exception {
        // 先创建CAS用户
        User casUser = new User();
        casUser.setUsername(MOCK_UID);
        casUser.setPassword("encoded_password");
        casUser.setRealName(MOCK_NAME);
        casUser.setRole(Constants.ROLE_STUDENT);
        casUser.setAuthType(Constants.AUTH_TYPE_CAS);
        casUser.setCasUid(MOCK_UID);
        casUser.setIsProfileComplete(1);
        casUser.setStatus(Constants.USER_STATUS_ENABLED);
        casUser.setCreateTime(java.time.LocalDateTime.now());
        casUser.setUpdateTime(java.time.LocalDateTime.now());
        userMapper.insert(casUser);

        MvcResult result = mockMvc.perform(get("/auth/cas/validate")
                .param("ticket", MOCK_TICKET))
                .andExpect(status().isFound()) // 302 重定向
                .andReturn();

        // 验证重定向到回调页面（已存在用户直接登录成功）
        String redirectUrl = result.getResponse().getRedirectedUrl();
        assertNotNull(redirectUrl);
        assertTrue(redirectUrl.contains("/cas-callback"));
        assertTrue(redirectUrl.contains("token="));
    }

    @Test
    @DisplayName("测试Mock模式 - 同名本地账号检测")
    void testDuplicateAccountDetection() throws Exception {
        // 创建同名本地账号
        User localUser = new User();
        localUser.setUsername("zhangsan_local");
        localUser.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO"); // encoded "password123"
        localUser.setRealName(MOCK_NAME);
        localUser.setRole(Constants.ROLE_STUDENT);
        localUser.setAuthType(Constants.AUTH_TYPE_LOCAL);
        localUser.setStatus(Constants.USER_STATUS_ENABLED);
        localUser.setIsProfileComplete(1);
        localUser.setCreateTime(java.time.LocalDateTime.now());
        localUser.setUpdateTime(java.time.LocalDateTime.now());
        userMapper.insert(localUser);

        MvcResult result = mockMvc.perform(get("/auth/cas/validate")
                .param("ticket", MOCK_TICKET))
                .andExpect(status().isFound()) // 302 重定向
                .andReturn();

        // 验证重定向到合并账号页面
        String redirectUrl = result.getResponse().getRedirectedUrl();
        assertNotNull(redirectUrl);
        assertTrue(redirectUrl.contains("/cas-merge"));
        assertTrue(redirectUrl.contains("data=")); // 包含合并所需数据
    }

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("测试账号合并 - 成功合并")
    void testMergeAccountSuccess() throws Exception {
        // 创建同名本地账号，使用正确的密码哈希
        String plainPassword = "password123";
        String encodedPassword = passwordEncoder.encode(plainPassword);
        User localUser = new User();
        localUser.setUsername("zhangsan_local");
        localUser.setPassword(encodedPassword);
        localUser.setRealName(MOCK_NAME);
        localUser.setRole(Constants.ROLE_STUDENT);
        localUser.setAuthType(Constants.AUTH_TYPE_LOCAL);
        localUser.setStatus(Constants.USER_STATUS_ENABLED);
        localUser.setIsProfileComplete(1);
        localUser.setCreateTime(java.time.LocalDateTime.now());
        localUser.setUpdateTime(java.time.LocalDateTime.now());
        userMapper.insert(localUser);

        CasMergeRequest mergeRequest = new CasMergeRequest();
        mergeRequest.setCasUid(MOCK_UID);
        mergeRequest.setRealName(MOCK_NAME);
        mergeRequest.setPassword(plainPassword);

        MvcResult result = mockMvc.perform(post("/auth/cas/merge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mergeRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Result<CasLoginResponse> response = objectMapper.readValue(content, 
                objectMapper.getTypeFactory().constructParametricType(Result.class, CasLoginResponse.class));
        
        assertEquals(200, response.getCode());
        CasLoginResponse loginResponse = response.getData();
        
        assertNotNull(loginResponse.getToken());
        assertFalse(loginResponse.getNeedMerge());
        assertEquals(Constants.AUTH_TYPE_BOTH, loginResponse.getUser().getAuthType());
        assertEquals(MOCK_UID, loginResponse.getUser().getCasUid());

        // 验证数据库已更新
        User mergedUser = userMapper.selectById(localUser.getId());
        assertEquals(Constants.AUTH_TYPE_BOTH, mergedUser.getAuthType());
        assertEquals(MOCK_UID, mergedUser.getCasUid());
    }

    @Test
    @DisplayName("测试创建新账号 - 跳过合并")
    void testCreateNewAccount() throws Exception {
        String uniqueUid = "2021999";
        String uniqueName = "TestUser";
        String ticket = "MOCK-" + uniqueUid + "-" + uniqueName;

        MvcResult result = mockMvc.perform(post("/auth/cas/create-new")
                .param("casUid", uniqueUid)
                .param("realName", uniqueName))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Result<CasLoginResponse> response = objectMapper.readValue(content, 
                objectMapper.getTypeFactory().constructParametricType(Result.class, CasLoginResponse.class));
        
        assertEquals(200, response.getCode());
        CasLoginResponse loginResponse = response.getData();
        
        assertNotNull(loginResponse.getToken());
        assertTrue(loginResponse.getNeedCompleteProfile());
        assertEquals(uniqueUid, loginResponse.getUser().getUsername());
        assertEquals(uniqueName, loginResponse.getUser().getRealName());

        // 验证用户已创建
        User dbUser = userMapper.selectByCasUid(uniqueUid);
        assertNotNull(dbUser);
        assertEquals(uniqueName, dbUser.getRealName());
    }

    @Test
    @DisplayName("测试完善用户资料")
    void testCompleteProfile() throws Exception {
        // 先创建CAS用户
        User casUser = new User();
        casUser.setUsername(MOCK_UID);
        casUser.setPassword("encoded_password");
        casUser.setRealName(MOCK_NAME);
        casUser.setRole(Constants.ROLE_STUDENT);
        casUser.setAuthType(Constants.AUTH_TYPE_CAS);
        casUser.setCasUid(MOCK_UID);
        casUser.setIsProfileComplete(0);
        casUser.setStatus(Constants.USER_STATUS_ENABLED);
        casUser.setCreateTime(java.time.LocalDateTime.now());
        casUser.setUpdateTime(java.time.LocalDateTime.now());
        userMapper.insert(casUser);

        // 生成token
        String token = jwtUtil.generateToken(casUser.getId(), casUser.getUsername(), casUser.getRole());

        CompleteProfileDTO profileDTO = new CompleteProfileDTO();
        profileDTO.setEmail("test@example.com");
        profileDTO.setPhone("13800138000");
        profileDTO.setCollegeId(1L);
        //profileDTO.setRole(Constants.ROLE_STUDENT);

        MvcResult result = mockMvc.perform(post("/auth/cas/complete-profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Result<?> response = objectMapper.readValue(content, Result.class);
        
        assertEquals(200, response.getCode());

        // 验证数据库已更新
        User updatedUser = userMapper.selectById(casUser.getId());
        assertEquals("test@example.com", updatedUser.getEmail());
        assertEquals("13800138000", updatedUser.getPhone());
        assertEquals(Integer.valueOf(1), updatedUser.getIsProfileComplete());
    }

    @Test
    @DisplayName("测试账号合并 - 密码错误")
    void testMergeAccountWrongPassword() throws Exception {
        // 创建同名本地账号，使用正确的密码哈希
        String plainPassword = "password123";
        String encodedPassword = passwordEncoder.encode(plainPassword);
        User localUser = new User();
        localUser.setUsername("zhangsan_local");
        localUser.setPassword(encodedPassword);
        localUser.setRealName(MOCK_NAME);
        localUser.setRole(Constants.ROLE_STUDENT);
        localUser.setAuthType(Constants.AUTH_TYPE_LOCAL);
        localUser.setStatus(Constants.USER_STATUS_ENABLED);
        localUser.setIsProfileComplete(1);
        localUser.setCreateTime(java.time.LocalDateTime.now());
        localUser.setUpdateTime(java.time.LocalDateTime.now());
        userMapper.insert(localUser);

        CasMergeRequest mergeRequest = new CasMergeRequest();
        mergeRequest.setCasUid(MOCK_UID);
        mergeRequest.setRealName(MOCK_NAME);
        mergeRequest.setPassword("wrongpassword");

        MvcResult result = mockMvc.perform(post("/auth/cas/merge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mergeRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Result<?> response = objectMapper.readValue(content, Result.class);
        
        // 应该返回错误
        assertNotEquals(200, response.getCode());
    }

    @Test
    @DisplayName("测试根据学号查询用户")
    void testGetUserByCasUid() throws Exception {
        // 创建管理员token
        String adminToken = jwtUtil.generateToken(1L, "admin", Constants.ROLE_SCHOOL_ADMIN);

        // 先创建CAS用户
        User casUser = new User();
        casUser.setUsername(MOCK_UID); // 学号作为用户名
        casUser.setPassword("encoded_password");
        casUser.setRealName(MOCK_NAME);
        casUser.setRole(Constants.ROLE_STUDENT);
        casUser.setAuthType(Constants.AUTH_TYPE_CAS);
        casUser.setCasUid(MOCK_UID);
        casUser.setIsProfileComplete(1);
        casUser.setStatus(Constants.USER_STATUS_ENABLED);
        casUser.setCreateTime(java.time.LocalDateTime.now());
        casUser.setUpdateTime(java.time.LocalDateTime.now());
        userMapper.insert(casUser);

        MvcResult result = mockMvc.perform(get("/users/cas/{casUid}", MOCK_UID)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Result<User> response = objectMapper.readValue(content, 
                objectMapper.getTypeFactory().constructParametricType(Result.class, User.class));
        
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals(MOCK_UID, response.getData().getCasUid());
        assertEquals(MOCK_NAME, response.getData().getRealName());
        assertEquals(MOCK_UID, response.getData().getUsername()); // 学号作为用户名
    }

    @Test
    @DisplayName("测试用户列表按学号查询")
    void testGetUserListByCasUid() throws Exception {
        // 创建管理员token
        String adminToken = jwtUtil.generateToken(1L, "admin", Constants.ROLE_SCHOOL_ADMIN);

        // 创建测试用户
        User casUser = new User();
        casUser.setUsername("2021999");
        casUser.setPassword("encoded_password");
        casUser.setRealName("测试学生");
        casUser.setRole(Constants.ROLE_STUDENT);
        casUser.setAuthType(Constants.AUTH_TYPE_CAS);
        casUser.setCasUid("2021999");
        casUser.setIsProfileComplete(1);
        casUser.setStatus(Constants.USER_STATUS_ENABLED);
        casUser.setCreateTime(java.time.LocalDateTime.now());
        casUser.setUpdateTime(java.time.LocalDateTime.now());
        userMapper.insert(casUser);

        MvcResult result = mockMvc.perform(get("/users")
                .header("Authorization", "Bearer " + adminToken)
                .param("casUid", "2021999"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Result<?> response = objectMapper.readValue(content, Result.class);
        
        assertEquals(200, response.getCode());
        // 验证返回结果中包含该用户
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> data = (java.util.Map<String, Object>) response.getData();
        @SuppressWarnings("unchecked")
        java.util.List<Object> list = (java.util.List<Object>) data.get("list");
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("测试获取CAS登出URL")
    void testGetCasLogoutUrl() throws Exception {
        MvcResult result = mockMvc.perform(get("/auth/cas/logout-url"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Result<?> response = objectMapper.readValue(content, Result.class);

        assertEquals(200, response.getCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.getData();
        assertNotNull(data);
        assertTrue((Boolean) data.get("enabled"));
        assertNotNull(data.get("logoutUrl"));
        assertNotNull(data.get("fullLogoutUrl"));
    }
}

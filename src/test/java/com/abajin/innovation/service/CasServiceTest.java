package com.abajin.innovation.service;

import com.abajin.innovation.common.Constants;
import com.abajin.innovation.config.CasConfig;
import com.abajin.innovation.dto.CasLoginResponse;
import com.abajin.innovation.dto.CompleteProfileDTO;
import com.abajin.innovation.entity.College;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.mapper.CollegeMapper;
import com.abajin.innovation.mapper.UserMapper;
import com.abajin.innovation.util.JwtUtil;
import com.abajin.innovation.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * CAS服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CasServiceTest {

    @Mock
    private CasConfig casConfig;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CollegeMapper collegeMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private CasService casService;

    private User mockUser;
    private College mockCollege;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("2021001");
        mockUser.setRealName("张三");
        mockUser.setRole(Constants.ROLE_STUDENT);
        mockUser.setAuthType(Constants.AUTH_TYPE_CAS);
        mockUser.setCasUid("2021001");
        mockUser.setStatus(Constants.USER_STATUS_ENABLED);
        mockUser.setIsProfileComplete(1);
        mockUser.setCollegeId(1L);
        mockUser.setCollegeName("计算机学院");

        mockCollege = new College();
        mockCollege.setId(1L);
        mockCollege.setName("计算机学院");
        mockCollege.setCode("CS");
    }

    @Test
    @DisplayName("测试CAS功能已启用")
    void testIsCasEnabled() {
        when(casConfig.getEnabled()).thenReturn(true);
        assertTrue(casService.isCasEnabled());
    }

    @Test
    @DisplayName("测试CAS功能未启用")
    void testIsCasDisabled() {
        when(casConfig.getEnabled()).thenReturn(false);
        assertFalse(casService.isCasEnabled());
    }

    @Test
    @DisplayName("测试Mock模式验证ticket")
    void testValidateMockTicket() {
        // 设置Mock模式
        when(casConfig.getMockMode()).thenReturn(true);

        String mockTicket = "MOCK-2021001-ZhangSan";
        String serviceUrl = "http://localhost:8080/api/auth/cas/validate";

        // 模拟用户已存在
        when(userMapper.selectByCasUid("2021001")).thenReturn(mockUser);
        when(jwtUtil.generateToken(anyLong(), anyString(), anyString())).thenReturn("mock-jwt-token");

        CasLoginResponse response = casService.validateTicketAndLogin(mockTicket, serviceUrl);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertFalse(response.getNeedCompleteProfile());
        assertFalse(response.getNeedMerge());
    }

    @Test
    @DisplayName("测试新用户登录需要完善资料")
    void testNewUserNeedCompleteProfile() {
        when(casConfig.getMockMode()).thenReturn(true);
        when(casConfig.getEnabled()).thenReturn(true);

        String mockTicket = "MOCK-2021002-LiSi";
        String serviceUrl = "http://localhost:8080/api/auth/cas/validate";

        // 模拟用户不存在
        when(userMapper.selectByCasUid("2021002")).thenReturn(null);
        // 模拟没有同名本地账号
        when(userMapper.selectByRealNameAndAuthType("LiSi", Constants.AUTH_TYPE_LOCAL))
            .thenReturn(Collections.emptyList());
        // 模拟查找学院（使用lenient避免不必要的stubbing警告）
        lenient().when(collegeMapper.selectAll()).thenReturn(Arrays.asList(mockCollege));
        // 模拟插入用户
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(jwtUtil.generateToken(anyLong(), anyString(), anyString())).thenReturn("mock-jwt-token");

        // 模拟insert操作，设置ID
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return 1;
        }).when(userMapper).insert(any(User.class));

        CasLoginResponse response = casService.validateTicketAndLogin(mockTicket, serviceUrl);

        assertNotNull(response);
        assertTrue(response.getNeedCompleteProfile());
        assertFalse(response.getNeedMerge());
        assertEquals("mock-jwt-token", response.getToken());
    }

    @Test
    @DisplayName("测试同名本地账号检测")
    void testDuplicateAccountDetection() {
        when(casConfig.getMockMode()).thenReturn(true);

        String mockTicket = "MOCK-2021001-ZhangSan";
        String serviceUrl = "http://localhost:8080/api/auth/cas/validate";

        // 模拟CAS用户不存在
        when(userMapper.selectByCasUid("2021001")).thenReturn(null);
        
        // 模拟存在同名本地账号
        User localUser = new User();
        localUser.setId(1L);
        localUser.setUsername("zhangsan");
        localUser.setRealName("ZhangSan");
        localUser.setAuthType(Constants.AUTH_TYPE_LOCAL);
        when(userMapper.selectByRealNameAndAuthType("ZhangSan", Constants.AUTH_TYPE_LOCAL))
            .thenReturn(Arrays.asList(localUser));

        CasLoginResponse response = casService.validateTicketAndLogin(mockTicket, serviceUrl);

        assertNotNull(response);
        assertTrue(response.getNeedMerge());
        assertNotNull(response.getDuplicateAccount());
        assertEquals("2021001", response.getCasUid());
        assertEquals("ZhangSan", response.getCasName());
    }

    @Test
    @DisplayName("测试账号合并成功")
    void testMergeAccountSuccess() {
        String casUid = "2021001";
        String realName = "张三";
        String password = "password123";

        // 模拟本地账号
        User localUser = new User();
        localUser.setId(1L);
        localUser.setUsername("zhangsan");
        localUser.setRealName(realName);
        localUser.setPassword("encoded-password");
        localUser.setAuthType(Constants.AUTH_TYPE_LOCAL);
        localUser.setRole(Constants.ROLE_STUDENT);
        localUser.setStatus(Constants.USER_STATUS_ENABLED);

        when(userMapper.selectByRealNameAndAuthType(realName, Constants.AUTH_TYPE_LOCAL))
            .thenReturn(Arrays.asList(localUser));
        when(userMapper.selectByCasUid(casUid)).thenReturn(null);
        when(passwordEncoder.matches(password, "encoded-password")).thenReturn(true);
        when(jwtUtil.generateToken(anyLong(), anyString(), anyString())).thenReturn("mock-jwt-token");

        CasLoginResponse response = casService.mergeAccountWithRealName(casUid, realName, password);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertFalse(response.getNeedMerge());
        
        // 验证用户被更新
        verify(userMapper).update(argThat(user -> 
            Constants.AUTH_TYPE_BOTH.equals(user.getAuthType()) &&
            casUid.equals(user.getCasUid())
        ));
    }

    @Test
    @DisplayName("测试账号合并密码错误")
    void testMergeAccountWrongPassword() {
        String casUid = "2021001";
        String realName = "张三";
        String password = "wrong-password";

        User localUser = new User();
        localUser.setId(1L);
        localUser.setRealName(realName);
        localUser.setPassword("encoded-password");
        localUser.setAuthType(Constants.AUTH_TYPE_LOCAL);

        when(userMapper.selectByRealNameAndAuthType(realName, Constants.AUTH_TYPE_LOCAL))
            .thenReturn(Arrays.asList(localUser));
        when(passwordEncoder.matches(password, "encoded-password")).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            casService.mergeAccountWithRealName(casUid, realName, password);
        });

        assertEquals("密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("测试账号合并本地账号不存在")
    void testMergeAccountLocalNotFound() {
        String casUid = "2021001";
        String realName = "不存在的用户";
        String password = "password123";

        // LOCAL 类型未找到
        when(userMapper.selectByRealNameAndAuthType(realName, Constants.AUTH_TYPE_LOCAL))
            .thenReturn(Collections.emptyList());
        // BOTH 类型也未找到
        when(userMapper.selectByRealNameAndAuthType(realName, Constants.AUTH_TYPE_BOTH))
            .thenReturn(Collections.emptyList());
        // 用户名查找也未找到
        when(userMapper.selectByUsername(casUid))
            .thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            casService.mergeAccountWithRealName(casUid, realName, password);
        });

        assertEquals("未找到对应的本地账号，请检查姓名是否正确或选择\"创建新账号\"", exception.getMessage());
    }

    @Test
    @DisplayName("测试创建新账号（跳过合并）")
    void testCreateNewAccountWithoutMerge() {
        String casUid = "2021003";
        String realName = "王五";

        when(userMapper.selectByCasUid(casUid)).thenReturn(null);
        when(userMapper.selectByUsername(casUid)).thenReturn(null);
        lenient().when(collegeMapper.selectAll()).thenReturn(Arrays.asList(mockCollege));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(jwtUtil.generateToken(anyLong(), anyString(), anyString())).thenReturn("mock-jwt-token");

        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(3L);
            return 1;
        }).when(userMapper).insert(any(User.class));

        CasLoginResponse response = casService.createNewAccountWithoutMerge(casUid, realName);

        assertNotNull(response);
        assertTrue(response.getNeedCompleteProfile());
        assertFalse(response.getNeedMerge());
        verify(userMapper).insert(any(User.class));
    }

    @Test
    @DisplayName("测试完善用户资料")
    void testCompleteProfile() {
        Long userId = 1L;
        
        CompleteProfileDTO dto = new CompleteProfileDTO();
        dto.setEmail("test@example.com");
        dto.setPhone("13800138000");
        dto.setCollegeId(1L);
        //dto.setRole(Constants.ROLE_STUDENT);

        User user = new User();
        user.setId(userId);
        user.setUsername("2021001");
        user.setRealName("张三");
        user.setIsProfileComplete(0);

        when(userMapper.selectById(userId)).thenReturn(user);
        when(collegeMapper.selectById(1L)).thenReturn(mockCollege);

        casService.completeProfile(userId, dto);

        assertEquals("test@example.com", user.getEmail());
        assertEquals("13800138000", user.getPhone());
        assertEquals(1L, user.getCollegeId());
        assertEquals("计算机学院", user.getCollegeName());
        // 角色由统一身份认证或注册时确定，不在完善资料时修改
        assertEquals(1, user.getIsProfileComplete());
        
        verify(userMapper).update(user);
    }

    @Test
    @DisplayName("测试完善资料用户不存在")
    void testCompleteProfileUserNotFound() {
        Long userId = 999L;
        
        CompleteProfileDTO dto = new CompleteProfileDTO();
        dto.setEmail("test@example.com");
        dto.setCollegeId(1L);

        when(userMapper.selectById(userId)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            casService.completeProfile(userId, dto);
        });

        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试已禁用账号登录")
    void testDisabledUserLogin() {
        when(casConfig.getMockMode()).thenReturn(true);

        String mockTicket = "MOCK-2021001-ZhangSan";
        String serviceUrl = "http://localhost:8080/api/auth/cas/validate";

        User disabledUser = new User();
        disabledUser.setId(1L);
        disabledUser.setUsername("2021001");
        disabledUser.setStatus(Constants.USER_STATUS_DISABLED);
        disabledUser.setAuthType(Constants.AUTH_TYPE_CAS);

        when(userMapper.selectByCasUid("2021001")).thenReturn(disabledUser);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            casService.validateTicketAndLogin(mockTicket, serviceUrl);
        });

        assertEquals("账户已被禁用", exception.getMessage());
    }

    @Test
    @DisplayName("测试CAS UID已被其他账号绑定")
    void testCasUidAlreadyBound() {
        String casUid = "2021001";
        String realName = "张三";
        String password = "password123";

        User localUser = new User();
        localUser.setId(1L);
        localUser.setRealName(realName);
        localUser.setPassword("encoded-password");
        localUser.setAuthType(Constants.AUTH_TYPE_LOCAL);

        User otherCasUser = new User();
        otherCasUser.setId(2L);
        otherCasUser.setCasUid(casUid);

        when(userMapper.selectByRealNameAndAuthType(realName, Constants.AUTH_TYPE_LOCAL))
            .thenReturn(Arrays.asList(localUser));
        when(userMapper.selectByCasUid(casUid)).thenReturn(otherCasUser);
        when(passwordEncoder.matches(password, "encoded-password")).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            casService.mergeAccountWithRealName(casUid, realName, password);
        });

        assertEquals("该统一身份认证账号已绑定其他用户", exception.getMessage());
    }

    @Test
    @DisplayName("测试存储ticket-token映射")
    void testStoreTicketTokenMapping() {
        String ticket = "ST-12345-abcde";
        String token = "mock-jwt-token";

        when(redisUtil.setNx(anyString(), eq(token), eq(7L), eq(TimeUnit.DAYS))).thenReturn(true);

        casService.storeTicketTokenMapping(ticket, token);

        verify(redisUtil).setNx(contains("cas:ticket:token"), eq(token), eq(7L), eq(TimeUnit.DAYS));
    }

    @Test
    @DisplayName("测试token加入黑名单")
    void testBlacklistToken() {
        String token = "mock-jwt-token";
        Date futureDate = new Date(System.currentTimeMillis() + 3600000); // 1小时后过期

        when(jwtUtil.getExpirationDateFromToken(token)).thenReturn(futureDate);
        when(redisUtil.setNx(anyString(), eq("1"), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);

        casService.blacklistToken(token);

        verify(redisUtil).setNx(contains("cas:token:blacklist"), eq("1"), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("测试检查token是否在黑名单中")
    void testIsTokenBlacklisted() {
        String token = "mock-jwt-token";

        when(redisUtil.exist(anyString())).thenReturn(true);

        boolean result = casService.isTokenBlacklisted(token);

        assertTrue(result);
        verify(redisUtil).exist(contains("cas:token:blacklist"));
    }

    @Test
    @DisplayName("测试检查token不在黑名单中")
    void testIsTokenNotBlacklisted() {
        String token = "mock-jwt-token";

        when(redisUtil.exist(anyString())).thenReturn(false);

        boolean result = casService.isTokenBlacklisted(token);

        assertFalse(result);
    }

    @Test
    @DisplayName("测试处理CAS单点登出")
    void testProcessCasLogout() {
        String ticket = "ST-12345-abcde";
        String token = "mock-jwt-token";
        Date futureDate = new Date(System.currentTimeMillis() + 3600000);

        when(redisUtil.get(anyString())).thenReturn(token);
        when(jwtUtil.getExpirationDateFromToken(token)).thenReturn(futureDate);
        when(redisUtil.setNx(anyString(), eq("1"), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);
        when(redisUtil.del(anyString())).thenReturn(true);

        casService.processCasLogout(ticket);

        verify(redisUtil).get(contains("cas:ticket:token"));
        verify(redisUtil).del(contains("cas:ticket:token"));
        verify(redisUtil).setNx(contains("cas:token:blacklist"), eq("1"), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("测试处理未知ticket的单点登出")
    void testProcessCasLogoutWithUnknownTicket() {
        String ticket = "ST-unknown";

        when(redisUtil.get(anyString())).thenReturn(null);

        casService.processCasLogout(ticket);

        verify(redisUtil).get(contains("cas:ticket:token"));
        verify(redisUtil, never()).del(anyString());
        verify(redisUtil, never()).setNx(anyString(), anyString(), anyLong(), any());
    }

    @Test
    @DisplayName("测试用户主动退出登录")
    void testUserLogout() {
        String token = "mock-jwt-token";
        Date futureDate = new Date(System.currentTimeMillis() + 3600000);

        when(jwtUtil.getExpirationDateFromToken(token)).thenReturn(futureDate);
        when(redisUtil.setNx(anyString(), eq("1"), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);

        casService.logout(token);

        verify(redisUtil).setNx(contains("cas:token:blacklist"), eq("1"), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("测试IP白名单校验-合法IP")
    void testIsValidCasServerIpValid() {
        when(casConfig.getMockMode()).thenReturn(false);
        when(casConfig.getServerIps()).thenReturn("10.0.0.1,10.0.0.2");

        assertTrue(casService.isValidCasServerIp("10.0.0.1"));
        assertTrue(casService.isValidCasServerIp("10.0.0.2"));
    }

    @Test
    @DisplayName("测试IP白名单校验-非法IP")
    void testIsValidCasServerIpInvalid() {
        when(casConfig.getMockMode()).thenReturn(false);
        when(casConfig.getServerIps()).thenReturn("10.0.0.1");

        assertFalse(casService.isValidCasServerIp("192.168.1.1"));
        assertFalse(casService.isValidCasServerIp("10.0.0.2"));
    }

    @Test
    @DisplayName("测试IP白名单校验-Mock模式跳过")
    void testIsValidCasServerIpMockMode() {
        when(casConfig.getMockMode()).thenReturn(true);

        assertTrue(casService.isValidCasServerIp("192.168.1.1"));
        assertTrue(casService.isValidCasServerIp("any-ip"));
    }

    @Test
    @DisplayName("测试IP白名单校验-未配置白名单")
    void testIsValidCasServerIpNoWhitelist() {
        when(casConfig.getMockMode()).thenReturn(false);
        when(casConfig.getServerIps()).thenReturn(null);

        assertFalse(casService.isValidCasServerIp("10.0.0.1"));
    }
}

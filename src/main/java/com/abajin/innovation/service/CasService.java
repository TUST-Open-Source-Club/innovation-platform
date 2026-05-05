package com.abajin.innovation.service;

import com.abajin.innovation.common.Constants;
import com.abajin.innovation.config.CasConfig;
import com.abajin.innovation.converter.LoginUserDTOConverter;
import com.abajin.innovation.dto.CasLoginResponse;
import com.abajin.innovation.dto.CompleteProfileDTO;
import com.abajin.innovation.entity.College;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.mapper.CollegeMapper;
import com.abajin.innovation.mapper.UserMapper;
import com.abajin.innovation.util.JwtUtil;
import com.abajin.innovation.util.RedisUtil;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;

import javax.net.ssl.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * CAS统一身份认证服务
 */
@Service
@Slf4j
public class CasService {

    @Autowired
    private CasConfig casConfig;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CollegeMapper collegeMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtil redisUtil;

    // Token黑名单Redis key前缀
    private static final String TOKEN_BLACKLIST_KEY = "cas:token:blacklist";
    // CAS用户ticket与token映射key前缀（用于单点登出时查找token）
    private static final String CAS_TICKET_TOKEN_KEY = "cas:ticket:token";

    /**
     * CAS用户信息封装类
     */
    public static class CasUserInfo {
        private String uid;        // 学号/工号
        private String cn;         // 姓名
        private String userName;   // 用户名
        private String college;    // 学院名称
        private String userType;   // 用户类型：student/teacher

        public String getUid() { return uid; }
        public void setUid(String uid) { this.uid = uid; }
        public String getCn() { return cn; }
        public void setCn(String cn) { this.cn = cn; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getCollege() { return college; }
        public void setCollege(String college) { this.college = college; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
    }

    /**
     * 验证CAS ticket并处理登录
     */
    @Transactional
    public CasLoginResponse validateTicketAndLogin(String ticket, String serviceUrl) {
        // 1. 验证ticket并获取用户信息
        CasUserInfo casUserInfo = validateTicket(ticket, serviceUrl);
        String casUid = casUserInfo.getUid();
        String cn = casUserInfo.getCn();

        // 2. 根据cas_uid查询用户
        User user = userMapper.selectByCasUid(casUid);

        if (user != null) {
            // 用户已存在，直接登录
            return handleExistingCasUser(user);
        }

        // 3. 用户不存在，检查是否有同名本地账号
        List<User> duplicateUsers = userMapper.selectByRealNameAndAuthType(cn, Constants.AUTH_TYPE_LOCAL);

        if (!duplicateUsers.isEmpty()) {
            // 有同名本地账号，返回合并提示
            CasLoginResponse response = new CasLoginResponse();
            response.setNeedMerge(true);
            response.setDuplicateAccount(LoginUserDTOConverter.convert(duplicateUsers.get(0)));
            response.setCasUid(casUid);
            response.setCasName(cn);
            return response;
        }

        // 4. 创建新的CAS用户
        return createNewCasUser(casUserInfo);
    }

    /**
     * 验证CAS ticket
     */
    private CasUserInfo validateTicket(String ticket, String serviceUrl) {
        // Mock模式：用于本地测试
        if (casConfig.getMockMode() && ticket.startsWith("MOCK-")) {
            return validateMockTicket(ticket);
        }

        // 如果配置了忽略SSL验证，设置信任所有证书（仅用于测试环境）
        if (casConfig.getIgnoreSslValidation() != null && casConfig.getIgnoreSslValidation()) {
            disableSslVerification();
        }

        // 真实CAS验证
        try {
            Cas20ServiceTicketValidator validator = new Cas20ServiceTicketValidator(
                    casConfig.getServerUrlPrefix()
            );
            validator.setEncoding("UTF-8");

            Assertion assertion = validator.validate(ticket, serviceUrl);
            AttributePrincipal principal = assertion.getPrincipal();
            Map<String, Object> attributes = principal.getAttributes();

            CasUserInfo userInfo = new CasUserInfo();
            userInfo.setUid(principal.getName()); // uid - 学号/工号
            userInfo.setCn((String) attributes.get("cn")); // 姓名
            userInfo.setUserName((String) attributes.get("user_name"));
            userInfo.setCollege((String) attributes.get("college")); // 学院
            userInfo.setUserType((String) attributes.get("user_type")); // 用户类型

            return userInfo;
        } catch (TicketValidationException e) {
            throw new RuntimeException("CAS ticket验证失败: " + e.getMessage());
        }
    }

    /**
     * 禁用SSL证书验证（仅用于测试环境）
     */
    private void disableSslVerification() {
        try {
            // 创建一个信任所有证书的TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            // 安装所有信任的SSL上下文
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // 创建所有主机名验证通过的HostnameVerifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        } catch (Exception e) {
            throw new RuntimeException("SSL验证禁用失败", e);
        }
    }

    /**
     * 验证Mock ticket（用于本地测试）
     */
    private CasUserInfo validateMockTicket(String ticket) {
        // 从ticket中提取用户信息：MOCK-{uid}-{name}
        String[] parts = ticket.split("-", 3);
        if (parts.length < 3) {
            throw new RuntimeException("Mock ticket格式错误");
        }

        CasUserInfo userInfo = new CasUserInfo();
        userInfo.setUid(parts[1]);
        userInfo.setCn(parts[2]);
        userInfo.setUserName(parts[2]);
        
        // Mock学院信息（可根据需要修改）
        userInfo.setCollege("计算机学院");
        userInfo.setUserType("student");

        return userInfo;
    }

    /**
     * 处理已存在的CAS用户登录
     */
    private CasLoginResponse handleExistingCasUser(User user) {
        // 检查账户状态
        if (user.getStatus() == Constants.USER_STATUS_DISABLED) {
            throw new RuntimeException("账户已被禁用");
        }

        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        CasLoginResponse response = new CasLoginResponse();
        response.setToken(token);
        response.setUser(LoginUserDTOConverter.convert(user));
        response.setNeedMerge(false);

        // 检查是否需要完善资料
        if (user.getIsProfileComplete() != null && user.getIsProfileComplete() == 0) {
            response.setNeedCompleteProfile(true);
        } else {
            response.setNeedCompleteProfile(false);
        }

        return response;
    }

    /**
     * 创建新的CAS用户
     */
    private CasLoginResponse createNewCasUser(CasUserInfo casUserInfo) {
        String casUid = casUserInfo.getUid();
        String cn = casUserInfo.getCn();
        String collegeName = casUserInfo.getCollege();

        User user = new User();
        user.setUsername(casUid); // 使用学号/工号作为用户名
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // 随机密码，不可用
        user.setRealName(cn);
        
        // 根据用户类型设置角色
        if ("teacher".equalsIgnoreCase(casUserInfo.getUserType())) {
            user.setRole(Constants.ROLE_TEACHER);
        } else {
            user.setRole(Constants.ROLE_STUDENT); // 默认学生角色
        }
        
        user.setAuthType(Constants.AUTH_TYPE_CAS);
        user.setCasUid(casUid);
        user.setIsProfileComplete(0); // 需要完善资料
        user.setStatus(Constants.USER_STATUS_ENABLED);
        
        // 设置学院信息（如果CAS返回了学院）
        if (collegeName != null && !collegeName.isEmpty()) {
            user.setCollegeName(collegeName);
            // 尝试查找匹配的学院ID
            College college = findCollegeByName(collegeName);
            if (college != null) {
                user.setCollegeId(college.getId());
            }
        }
        
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);

        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        CasLoginResponse response = new CasLoginResponse();
        response.setToken(token);
        response.setUser(LoginUserDTOConverter.convert(user));
        response.setNeedCompleteProfile(true);
        response.setNeedMerge(false);

        return response;
    }

    /**
     * 根据学院名称查找学院
     */
    private College findCollegeByName(String collegeName) {
        // 首先精确匹配
        List<College> colleges = collegeMapper.selectAll();
        for (College college : colleges) {
            if (college.getName().equals(collegeName)) {
                return college;
            }
        }
        // 然后模糊匹配
        for (College college : colleges) {
            if (college.getName().contains(collegeName) || collegeName.contains(college.getName())) {
                return college;
            }
        }
        return null;
    }

    /**
     * 合并本地账号（完整版本）
     * 将本地账号升级为支持CAS认证
     */
    @Transactional
    public CasLoginResponse mergeAccountWithRealName(String casUid, String realName, String password) {
        // 1. 查找本地账号（按姓名查找，不限制认证类型，只要不是纯CAS账号即可）
        List<User> localUsers = userMapper.selectByRealNameAndAuthType(realName, Constants.AUTH_TYPE_LOCAL);
        
        // 如果没找到 LOCAL 类型，尝试查找 BOTH 类型（可能已经合并过）
        if (localUsers.isEmpty()) {
            localUsers = userMapper.selectByRealNameAndAuthType(realName, Constants.AUTH_TYPE_BOTH);
        }
        
        // 如果还没找到，尝试根据用户名查找（姓名可能不同，但学号/用户名可能匹配）
        if (localUsers.isEmpty()) {
            // 尝试查找用户名与 casUid 相同的本地账号
            User userByUsername = userMapper.selectByUsername(casUid);
            if (userByUsername != null && 
                !Constants.AUTH_TYPE_CAS.equals(userByUsername.getAuthType())) {
                localUsers = java.util.Collections.singletonList(userByUsername);
            }
        }

        if (localUsers.isEmpty()) {
            throw new RuntimeException("未找到对应的本地账号，请检查姓名是否正确或选择\"创建新账号\"");
        }

        User localUser = localUsers.get(0);

        // 2. 验证密码
        if (!passwordEncoder.matches(password, localUser.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 3. 检查是否已经有其他账号绑定了这个CAS UID
        User existingCasUser = userMapper.selectByCasUid(casUid);
        if (existingCasUser != null && !existingCasUser.getId().equals(localUser.getId())) {
            throw new RuntimeException("该统一身份认证账号已绑定其他用户");
        }

        // 4. 更新账号为双认证模式
        localUser.setAuthType(Constants.AUTH_TYPE_BOTH);
        localUser.setCasUid(casUid);
        localUser.setUpdateTime(LocalDateTime.now());
        userMapper.update(localUser);

        // 5. 生成JWT token
        String token = jwtUtil.generateToken(localUser.getId(), localUser.getUsername(), localUser.getRole());

        CasLoginResponse response = new CasLoginResponse();
        response.setToken(token);
        response.setUser(LoginUserDTOConverter.convert(localUser));
        response.setNeedCompleteProfile(false);
        response.setNeedMerge(false);

        return response;
    }

    /**
     * 创建新账号（跳过合并）
     * 当用户选择不合并时，创建一个新的CAS账号
     */
    @Transactional
    public CasLoginResponse createNewAccountWithoutMerge(String casUid, String realName) {
        // 检查是否已存在
        User existingUser = userMapper.selectByCasUid(casUid);
        if (existingUser != null) {
            return handleExistingCasUser(existingUser);
        }

        // 创建新用户，使用不同的用户名避免冲突
        User user = new User();
        // 如果学号已被占用，添加CAS前缀
        String username = casUid;
        int suffix = 0;
        while (userMapper.selectByUsername(username) != null) {
            suffix++;
            username = casUid + "_cas" + suffix;
        }
        
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setRealName(realName);
        user.setRole(Constants.ROLE_STUDENT);
        user.setAuthType(Constants.AUTH_TYPE_CAS);
        user.setCasUid(casUid);
        user.setIsProfileComplete(0);
        user.setStatus(Constants.USER_STATUS_ENABLED);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        CasLoginResponse response = new CasLoginResponse();
        response.setToken(token);
        response.setUser(LoginUserDTOConverter.convert(user));
        response.setNeedCompleteProfile(true);
        response.setNeedMerge(false);

        return response;
    }

    /**
     * 完善用户资料
     */
    @Transactional
    public void completeProfile(Long userId, CompleteProfileDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新用户信息
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getCollegeId() != null) {
            user.setCollegeId(dto.getCollegeId());
            College college = collegeMapper.selectById(dto.getCollegeId());
            if (college != null) {
                user.setCollegeName(college.getName());
            }
        }
        // 注意：角色由统一身份认证或注册时确定，不允许在完善资料时修改
        // 本地注册默认为学生，CAS登录根据返回的userType确定

        // 标记资料已完善
        user.setIsProfileComplete(1);
        user.setUpdateTime(LocalDateTime.now());

        userMapper.update(user);
    }

    /**
     * 检查CAS功能是否启用
     */
    public boolean isCasEnabled() {
        return casConfig.getEnabled() != null && casConfig.getEnabled();
    }

    /**
     * 用户主动退出登录
     * 将当前token加入黑名单，使其立即失效
     *
     * @param token JWT token
     */
    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            blacklistToken(token);
            log.info("用户主动退出登录，token已加入黑名单");
        }
    }

    /**
     * 校验请求IP是否在CAS服务器白名单中
     *
     * @param ip 请求来源IP
     * @return true 如果在白名单中或Mock模式下
     */
    public boolean isValidCasServerIp(String ip) {
        // Mock模式下跳过IP校验（本地测试）
        if (casConfig.getMockMode() != null && casConfig.getMockMode()) {
            return true;
        }

        // 未启用IP白名单校验时直接放行
        if (casConfig.getServerIpWhitelistEnabled() == null || !casConfig.getServerIpWhitelistEnabled()) {
            log.info("CAS服务器IP白名单校验已关闭，放行单点登出请求");
            return true;
        }

        String serverIps = casConfig.getServerIps();
        if (serverIps == null || serverIps.isEmpty()) {
            log.warn("未配置CAS服务器IP白名单，拒绝单点登出请求");
            return false;
        }

        if (ip == null || ip.isEmpty()) {
            return false;
        }

        List<String> allowedIps = Arrays.asList(serverIps.split(","));
        return allowedIps.stream()
                .map(String::trim)
                .anyMatch(allowedIp -> allowedIp.equals(ip));
    }

    /**
     * 获取客户端真实IP地址
     * 支持X-Forwarded-For等代理头部
     *
     * @param request HTTP请求
     * @return 客户端真实IP
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 如果存在多个IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 存储CAS ticket与token的映射关系
     * 用于单点登出时根据ticket找到对应的token
     *
     * @param ticket CAS ticket
     * @param token  JWT token
     */
    public void storeTicketTokenMapping(String ticket, String token) {
        if (ticket == null || token == null) {
            return;
        }
        try {
            // 使用ticket的hash作为key，存储token
            String ticketHash = Integer.toHexString(ticket.hashCode());
            // 设置过期时间为7天（CAS ticket通常有效期较短）
            redisUtil.setNx(CAS_TICKET_TOKEN_KEY + ":" + ticketHash, token, 7L, java.util.concurrent.TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("存储CAS ticket映射失败: {}", e.getMessage());
        }
    }

    /**
     * 将token加入黑名单
     *
     * @param token JWT token
     */
    public void blacklistToken(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }
        try {
            // 获取token的过期时间
            long expiration = jwtUtil.getExpirationDateFromToken(token).getTime();
            long now = System.currentTimeMillis();
            long ttl = expiration - now;
            
            if (ttl > 0) {
                // 使用token的hash作为key，存储到Redis，过期时间为token剩余有效期
                String tokenHash = Integer.toHexString(token.hashCode());
                redisUtil.setNx(TOKEN_BLACKLIST_KEY + ":" + tokenHash, "1", ttl, java.util.concurrent.TimeUnit.MILLISECONDS);
                log.info("Token已加入黑名单，剩余有效期: {}ms", ttl);
            }
        } catch (Exception e) {
            log.warn("Token加入黑名单失败: {}", e.getMessage());
        }
    }

    /**
     * 检查token是否在黑名单中
     *
     * @param token JWT token
     * @return true 如果在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        try {
            String tokenHash = Integer.toHexString(token.hashCode());
            return redisUtil.exist(TOKEN_BLACKLIST_KEY + ":" + tokenHash);
        } catch (Exception e) {
            log.warn("检查Token黑名单失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 处理CAS单点登出
     * 根据ticket找到对应的token并加入黑名单
     *
     * @param ticket CAS ticket
     */
    public void processCasLogout(String ticket) {
        if (ticket == null || ticket.isEmpty()) {
            log.warn("CAS单点登出失败：ticket为空");
            return;
        }
        
        try {
            String ticketHash = Integer.toHexString(ticket.hashCode());
            String token = redisUtil.get(CAS_TICKET_TOKEN_KEY + ":" + ticketHash);
            
            if (token != null && !token.isEmpty()) {
                // 将token加入黑名单
                blacklistToken(token);
                // 删除映射关系
                redisUtil.del(CAS_TICKET_TOKEN_KEY + ":" + ticketHash);
                log.info("CAS单点登出成功，token已加入黑名单");
            } else {
                log.warn("CAS单点登出：未找到ticket对应的token，ticketHash={}", ticketHash);
            }
        } catch (Exception e) {
            log.error("CAS单点登出处理失败: {}", e.getMessage(), e);
        }
    }
}

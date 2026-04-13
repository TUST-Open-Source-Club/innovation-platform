package com.abajin.innovation.controller;

import com.abajin.innovation.common.Result;
import com.abajin.innovation.config.CasConfig;
import com.abajin.innovation.dto.CasLoginResponse;
import com.abajin.innovation.dto.CasMergeRequest;
import com.abajin.innovation.dto.CompleteProfileDTO;
import com.abajin.innovation.dto.LoginUserDTO;
import com.abajin.innovation.service.CasService;
import com.abajin.innovation.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * CAS统一身份认证Controller
 */
@RestController
@RequestMapping("/auth/cas")
@Slf4j
public class CasAuthController {

    @Autowired
    private CasConfig casConfig;

    @Autowired
    private CasService casService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取CAS功能状态
     */
    @GetMapping("/status")
    public Result<Map<String, Object>> getStatus() {
        Map<String, Object> data = new HashMap<>();
        boolean enabled = casService.isCasEnabled();
        data.put("enabled", enabled);
        if (enabled) {
            data.put("loginUrl", casConfig.getServerLoginUrl());
            data.put("mockMode", casConfig.getMockMode());
        }
        return Result.success(data);
    }

    /**
     * 发起CAS登录
     * 重定向到CAS服务器登录页面
     */
    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        if (!casService.isCasEnabled()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CAS功能未启用");
            return;
        }

        // 获取配置并处理斜杠问题
        String clientHostUrl = casConfig.getClientHostUrl();
        // 移除末尾的斜杠避免重叠
        if (clientHostUrl.endsWith("/")) {
            clientHostUrl = clientHostUrl.substring(0, clientHostUrl.length() - 1);
        }
        
        // 前端基础地址（移除 /api 后缀）
        String frontendBaseUrl = clientHostUrl.replace("/api", "");
        // 确保前端地址不以斜杠结尾
        if (frontendBaseUrl.endsWith("/")) {
            frontendBaseUrl = frontendBaseUrl.substring(0, frontendBaseUrl.length() - 1);
        }

        // Mock模式：直接重定向到前端回调页面，带上mock ticket
        if (casConfig.getMockMode()) {
            String mockTicket = "MOCK-2021001-ZhangSan";
            String redirectUrl = frontendBaseUrl + "/cas-callback?ticket=" + mockTicket;
            log.info("[CAS Mock] 重定向到回调页面: {}", redirectUrl);
            response.sendRedirect(redirectUrl);
            return;
        }

        // 构造回调地址（后端验证接口）
        String serviceUrl = clientHostUrl + "/auth/cas/validate";
        String encodedServiceUrl = URLEncoder.encode(serviceUrl, StandardCharsets.UTF_8);

        // 重定向到CAS登录页面
        String casLoginUrl = casConfig.getServerLoginUrl() + "?service=" + encodedServiceUrl;
        log.info("[CAS] 重定向到CAS登录页面: {}", casLoginUrl);
        response.sendRedirect(casLoginUrl);
    }

    /**
     * CAS回调验证ticket
     * CAS服务器会重定向到这个地址，并携带ticket参数
     * 验证成功后重定向到前端页面
     */
    @GetMapping("/validate")
    public void validate(
            @RequestParam("ticket") String ticket,
            HttpServletResponse response) throws IOException {
        log.info("[CAS] 收到ticket验证请求");
        
        // 获取配置并处理斜杠问题
        String clientHostUrl = casConfig.getClientHostUrl();
        if (clientHostUrl.endsWith("/")) {
            clientHostUrl = clientHostUrl.substring(0, clientHostUrl.length() - 1);
        }
        
        // 前端基础地址（移除 /api 后缀）
        String frontendBaseUrl = clientHostUrl.replace("/api", "");
        if (frontendBaseUrl.endsWith("/")) {
            frontendBaseUrl = frontendBaseUrl.substring(0, frontendBaseUrl.length() - 1);
        }
        
        try {
            if (!casService.isCasEnabled()) {
                log.warn("[CAS] 功能未启用");
                response.sendRedirect(frontendBaseUrl + "/login?error=cas_disabled");
                return;
            }

            // 构造service URL（必须与login时的一致）
            String serviceUrl = casConfig.getClientHostUrl() + "/auth/cas/validate";

            // 验证ticket并处理登录
            CasLoginResponse casResponse = casService.validateTicketAndLogin(ticket, serviceUrl);
            
            // 处理需要合并账号的情况
            if (casResponse.getNeedMerge() != null && casResponse.getNeedMerge()) {
                log.info("[CAS] 检测到同名账号，需要合并: casUid={}, realName={}", 
                    casResponse.getCasUid(), casResponse.getCasName());
                
                // 将合并信息编码后传给前端
                String mergeData = Base64.getUrlEncoder().encodeToString(
                    String.format("{\"casUid\":\"%s\",\"casName\":\"%s\",\"duplicateAccount\":{\"username\":\"%s\",\"realName\":\"%s\",\"role\":\"%s\"}}",
                        casResponse.getCasUid(),
                        casResponse.getCasName(),
                        casResponse.getDuplicateAccount() != null ? casResponse.getDuplicateAccount().getUsername() : "",
                        casResponse.getDuplicateAccount() != null ? casResponse.getDuplicateAccount().getRealName() : casResponse.getCasName(),
                        casResponse.getDuplicateAccount() != null ? casResponse.getDuplicateAccount().getRole() : "STUDENT"
                    ).getBytes(StandardCharsets.UTF_8)
                );
                response.sendRedirect(frontendBaseUrl + "/cas-merge?data=" + mergeData);
                return;
            }
            
            // 处理需要完善资料的情况
            if (casResponse.getNeedCompleteProfile() != null && casResponse.getNeedCompleteProfile()) {
                log.info("[CAS] 新用户登录，需要完善资料: userId={}", 
                    casResponse.getUser() != null ? casResponse.getUser().getId() : "unknown");
                
                // 将token传给前端（完善资料页面需要token进行认证）
                response.sendRedirect(frontendBaseUrl + "/complete-profile?token=" + 
                    URLEncoder.encode(casResponse.getToken(), StandardCharsets.UTF_8));
                return;
            }
            
            // 正常登录成功
            log.info("[CAS] 用户登录成功: userId={}", 
                casResponse.getUser() != null ? casResponse.getUser().getId() : "unknown");
            
            // 将token传给前端回调页面
            response.sendRedirect(frontendBaseUrl + "/cas-callback?ticket=success&token=" + 
                URLEncoder.encode(casResponse.getToken(), StandardCharsets.UTF_8));
            
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            log.error("[CAS] 登录失败: {}", errorMsg, e);
            
            // 优化错误信息，使其更友好
            String friendlyError;
            if (errorMsg == null) {
                friendlyError = "未知错误";
            } else if (errorMsg.contains("ticket验证失败")) {
                friendlyError = "认证票据验证失败，请重试";
            } else if (errorMsg.contains("SSL") || errorMsg.contains("证书")) {
                friendlyError = "无法建立安全连接，请检查网络或联系管理员";
            } else if (errorMsg.contains("账户已被禁用")) {
                friendlyError = "账户已被禁用，请联系管理员";
            } else {
                friendlyError = errorMsg;
            }
            
            response.sendRedirect(frontendBaseUrl + "/login-error?error=" + 
                URLEncoder.encode(friendlyError, StandardCharsets.UTF_8));
        }
    }

    /**
     * 合并本地账号
     * 当检测到同名本地账号时，用户可以选择合并
     */
    @PostMapping("/merge")
    public Result<CasLoginResponse> mergeAccount(@Valid @RequestBody CasMergeRequest request) {
        log.info("[CAS] 账号合并请求: casUid={}, realName={}", request.getCasUid(), request.getRealName());
        try {
            if (!casService.isCasEnabled()) {
                return Result.error("CAS功能未启用");
            }

            CasLoginResponse response = casService.mergeAccountWithRealName(
                request.getCasUid(), 
                request.getRealName(), 
                request.getPassword()
            );
            
            log.info("[CAS] 账号合并成功: userId={}", 
                response.getUser() != null ? response.getUser().getId() : "unknown");
            return Result.success(response);

        } catch (Exception e) {
            log.error("[CAS] 账号合并失败: {}", e.getMessage());
            return Result.error("账号合并失败: " + e.getMessage());
        }
    }

    /**
     * 创建新账号（跳过合并）
     * 当用户选择不合并同名账号时，创建一个新的CAS账号
     */
    @PostMapping("/create-new")
    public Result<CasLoginResponse> createNewAccount(
            @RequestParam("casUid") String casUid,
            @RequestParam("realName") String realName) {
        log.info("[CAS] 创建新账号请求: casUid={}, realName={}", casUid, realName);
        try {
            if (!casService.isCasEnabled()) {
                return Result.error("CAS功能未启用");
            }

            CasLoginResponse response = casService.createNewAccountWithoutMerge(casUid, realName);
            
            log.info("[CAS] 新账号创建成功: userId={}", 
                response.getUser() != null ? response.getUser().getId() : "unknown");
            return Result.success(response);

        } catch (Exception e) {
            log.error("[CAS] 创建账号失败: {}", e.getMessage());
            return Result.error("创建账号失败: " + e.getMessage());
        }
    }

    /**
     * 完善用户资料
     * CAS新用户首次登录后需要完善邮箱、手机号、学院等信息
     */
    @PostMapping("/complete-profile")
    public Result<Map<String, Object>> completeProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CompleteProfileDTO dto) {
        log.info("[CAS] 完善资料请求");
        try {
            if (!casService.isCasEnabled()) {
                return Result.error("CAS功能未启用");
            }

            // 从token中获取用户ID
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.error("无效的认证头");
            }
            
            String token = authHeader.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);

            if (userId == null) {
                return Result.error("无效的token");
            }

            casService.completeProfile(userId, dto);
            
            log.info("[CAS] 资料完善成功: userId={}", userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "资料完善成功");
            result.put("profileComplete", true);
            
            return Result.success(result);

        } catch (Exception e) {
            log.error("[CAS] 资料完善失败: {}", e.getMessage());
            return Result.error("资料完善失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否需要完善资料
     */
    @GetMapping("/check-profile")
    public Result<Map<String, Object>> checkProfile(
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.error("无效的认证头");
            }
            
            String token = authHeader.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(token);

            if (userId == null) {
                return Result.error("无效的token");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("needComplete", false);
            
            return Result.success(result);

        } catch (Exception e) {
            log.error("[CAS] 检查资料状态失败: {}", e.getMessage());
            return Result.error("检查失败: " + e.getMessage());
        }
    }

    /**
     * CAS单点登出回调
     * CAS服务器在用户登出时会调用此接口
     */
    @PostMapping("/logout")
    public Result<Void> casLogout(@RequestBody(required = false) Map<String, String> body) {
        log.info("[CAS] 收到单点登出请求");
        try {
            return Result.success("登出成功", null);
        } catch (Exception e) {
            log.error("[CAS] 登出处理失败: {}", e.getMessage());
            return Result.error("登出失败: " + e.getMessage());
        }
    }
}

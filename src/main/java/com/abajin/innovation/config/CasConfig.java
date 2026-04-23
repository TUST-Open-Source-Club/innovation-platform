package com.abajin.innovation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CAS配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "cas")
public class CasConfig {
    /**
     * 是否启用CAS功能
     */
    private Boolean enabled = false;

    /**
     * CAS服务器地址前缀
     */
    private String serverUrlPrefix;

    /**
     * CAS服务器登录地址
     */
    private String serverLoginUrl;

    /**
     * CAS服务器登出地址
     */
    private String serverLogoutUrl;

    /**
     * 本应用地址
     */
    private String clientHostUrl;

    /**
     * Mock模式（用于本地测试，无需真实CAS服务器）
     */
    private Boolean mockMode = false;

    /**
     * 忽略SSL证书验证（仅用于测试环境，生产环境请导入正确证书）
     */
    private Boolean ignoreSslValidation = false;

    /**
     * CAS服务器IP白名单（逗号分隔），用于校验单点登出回调请求的来源
     * 例如：10.0.0.1,10.0.0.2
     */
    private String serverIps;

    /**
     * 是否启用CAS服务器IP白名单校验
     * 生产环境建议开启，内网环境或无CAS服务器IP时可关闭
     */
    private Boolean serverIpWhitelistEnabled = true;

    /**
     * 获取CAS服务器登出地址
     * 如果没有配置，则使用默认构造：serverUrlPrefix + /logout
     */
    public String getServerLogoutUrl() {
        if (serverLogoutUrl != null && !serverLogoutUrl.isEmpty()) {
            return serverLogoutUrl;
        }
        if (serverUrlPrefix != null && !serverUrlPrefix.isEmpty()) {
            return serverUrlPrefix + "/logout";
        }
        return null;
    }
}

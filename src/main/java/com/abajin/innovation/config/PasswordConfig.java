package com.abajin.innovation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器配置类
 * 使用 Argon2id 算法，符合安全规范要求
 * Argon2id 参数说明：
 * - saltLength: 16字节
 * - hashLength: 32字节
 * - parallelism: 1
 * - memory: 65536KB (64MB)
 * - iterations: 3
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 65536, 4);
    }
}

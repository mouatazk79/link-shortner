package com.klaa.springboot4demo.security.code;

import com.klaa.springboot4demo.security.AbstractSecurityConfiguration;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class AuthCodeExchangeSecurityConfiguration extends AbstractSecurityConfiguration {
    private static final String AUTH_CODE_EXCHANGE = "/auth/code/exchange";

    @SneakyThrows
    @Bean("authCodeExchangeSecurityConfig")
    @Order(HIGHEST_PRECEDENCE + 30_000)
    public SecurityFilterChain authCodeExchangeSecurityChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) {
        http
                .securityMatcher(AUTH_CODE_EXCHANGE)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, AUTH_CODE_EXCHANGE).permitAll()
                        .anyRequest().denyAll()
                );
        return buildWithDefaults(http);
    }
}

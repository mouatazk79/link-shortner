package com.klaa.springboot4demo.security;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpMethod.GET;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class DefaultSecurityConfig extends AbstractSecurityConfiguration {
    private static final String ACTUATOR_PATTERN="/actuator/**";
    private static final String ERROR_PATTERN="/error";
    private static final String ACTUATOR_SECURITY_FILTER_CHAIN="actuatorSecurityConfig";
    private static final String ERROR_SECURITY_FILTER_CHAIN="errorSecurityConfig";
    private static final String DENY_ALL_SECURITY_FILTER_CHAIN="denyAllSecurityConfig";

    @SneakyThrows
    @Order(HIGHEST_PRECEDENCE+1_000)
    @Bean(ACTUATOR_SECURITY_FILTER_CHAIN)
    public SecurityFilterChain actuatorSecurityConfig(HttpSecurity http)  {
        http.securityMatcher(ACTUATOR_PATTERN).authorizeHttpRequests(
                authorizeRequests ->
                        authorizeRequests.requestMatchers(GET).permitAll()
        );
        return buildWithDefaults(http);
    }

    @SneakyThrows
    @Order(HIGHEST_PRECEDENCE+2_000)
    @Bean(ERROR_SECURITY_FILTER_CHAIN)
    public SecurityFilterChain errorSecurityConfig(HttpSecurity http)  {
        http.securityMatcher(ERROR_PATTERN).authorizeHttpRequests(
                authorizeRequests ->
                        authorizeRequests.requestMatchers(GET).permitAll()
        );
        return buildWithDefaults(http);
    }

    @SneakyThrows
    @Order(HIGHEST_PRECEDENCE+50_000)
    @Bean(DENY_ALL_SECURITY_FILTER_CHAIN)
    public SecurityFilterChain denyAllSecurityConfig(HttpSecurity http)  {
        http.authorizeHttpRequests(
                authorizeRequests ->
                        authorizeRequests.anyRequest().denyAll()
        );
        return buildWithDefaults(http);
    }




}

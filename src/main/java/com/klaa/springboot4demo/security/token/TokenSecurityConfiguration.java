package com.klaa.springboot4demo.security.token;

import com.klaa.springboot4demo.security.AbstractSecurityConfiguration;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Configuration(proxyBeanMethods = false)
public class TokenSecurityConfiguration extends AbstractSecurityConfiguration {
    private static final String TOKEN_SECURITY_FILTER_CHAIN="tokenSecurityFilterChain";
    private static final String TOKEN_REFRESH_PATTERN="/token/**";

    @SneakyThrows
    @Bean(TOKEN_SECURITY_FILTER_CHAIN)
    @Order(HIGHEST_PRECEDENCE + 9_000)
    public SecurityFilterChain tokenSecurityFilterChain(HttpSecurity http,
                                                        RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider) {
        val authenticationManager=buildAuthenticationManager(http,refreshTokenAuthenticationProvider);
        val filter=builAuthenticationFilter(authenticationManager);
        http.securityMatcher(TOKEN_REFRESH_PATTERN)
                .authenticationManager(authenticationManager)
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(
                        req->
                                req.requestMatchers(TOKEN_REFRESH_PATTERN).authenticated()
                                        .anyRequest().authenticated()
                );

        return buildWithDefaults(http);
    }

    private AuthenticationFilter builAuthenticationFilter(AuthenticationManager authenticationManager) {
        val filter=new AuthenticationFilter(authenticationManager,new RefreshTokenAuthenticationConverter());
        filter.setSuccessHandler((req, res, auth) -> {
            SecurityContextHolder.getContext().setAuthentication(auth);
        });

        filter.setFailureHandler((req, res, exception) -> {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.getWriter().write("{\"error\":\"Invalid refresh token\"}");
        });

        return filter;
    }

    private AuthenticationManager buildAuthenticationManager(HttpSecurity http, RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider) {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(refreshTokenAuthenticationProvider)
                .build();
    }


}

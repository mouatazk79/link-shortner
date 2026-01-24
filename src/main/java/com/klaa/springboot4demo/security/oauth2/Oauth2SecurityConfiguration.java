package com.klaa.springboot4demo.security.oauth2;

import com.klaa.springboot4demo.security.AbstractSecurityConfiguration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Oauth2SecurityConfiguration extends AbstractSecurityConfiguration {
    private static final String OAUTH2_CALLBACK_PATTERN="/login/oauth2/**";
    private static final String OAUTH2_AUTHORIZATION_PATTERN="/oauth2/**";
    private static final String OAUTH2_SECURITY_FILTER_CHAIN="oauth2SecurityFilterChain";
    Oauth2SuccessHandler oauth2SuccessHandler;


    @SneakyThrows
    @Order(HIGHEST_PRECEDENCE+6_000)
    @Bean(OAUTH2_SECURITY_FILTER_CHAIN)
    public SecurityFilterChain actuatorSecurityConfig(HttpSecurity http)  {
        http.securityMatcher(OAUTH2_CALLBACK_PATTERN,OAUTH2_AUTHORIZATION_PATTERN).authorizeHttpRequests(
                authorizeRequests ->
                        authorizeRequests.anyRequest().authenticated()
        ).oauth2Login(oauth2->
                oauth2.successHandler(oauth2SuccessHandler));
        return buildWithDefaults(http);
    }
}

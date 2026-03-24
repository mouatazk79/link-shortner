package com.klaa.springboot4demo.security.ott;

import com.klaa.springboot4demo.security.AbstractSecurityConfiguration;
import com.klaa.springboot4demo.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.authentication.ott.JdbcOneTimeTokenService;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationProvider;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class OneTimeTokenSecurityConfig extends AbstractSecurityConfiguration {
    OneTimeTokenSender oneTimeTokenSender;
    OneTimeTokenLoginGenerationSuccessHandler oneTimeTokenLoginGenerationSuccessHandler;
    private static final String OTT_LOGIN="/ott/login";
    private static final String OTT_GENERATION="/ott/generate";
    private static final String OTT_SECURITY_FILTER_CHAIN="ottSecurityFilterChain";

    private static final RequestMatcher OTT_LOGIN_MATCHER= PathPatternRequestMatcher.withDefaults().matcher(OTT_LOGIN);
    private static final RequestMatcher OTT_GENERATION_MATCHER=PathPatternRequestMatcher.withDefaults().matcher(OTT_GENERATION);


    @Bean(OTT_SECURITY_FILTER_CHAIN)
    @SneakyThrows
    @Order(HIGHEST_PRECEDENCE+7_000)
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           OneTimeTokenService oneTimeTokenService,
                                           OneTimeTokenAuthenticationProvider ottAuthenticationProvider) {
        http.securityMatcher(OTT_LOGIN, OTT_GENERATION)
                .authenticationProvider(ottAuthenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(OTT_LOGIN_MATCHER).permitAll()
                        .requestMatchers(OTT_GENERATION_MATCHER).permitAll()
                        .anyRequest().authenticated()
                )
                .oneTimeTokenLogin(
                        ott->ott
                                .showDefaultSubmitPage(false)
                                .tokenGeneratingUrl(OTT_GENERATION)
                                .tokenService(oneTimeTokenService)
                                .tokenGenerationSuccessHandler(oneTimeTokenSender)
                                .loginProcessingUrl(OTT_LOGIN)
                                .successHandler(oneTimeTokenLoginGenerationSuccessHandler)

                );

        return buildWithDefaults(http);
    }

    @Bean
    public OneTimeTokenAuthenticationProvider ottAuthenticationProvider(OneTimeTokenService oneTimeTokenService,
                                                                        ConversionService conversionService,
                                                                        UserService userService) {
        val userDetailsService =new OneTimeTokenProvisioningUserDetailsService(userService,conversionService);

        return  new OneTimeTokenAuthenticationProvider(oneTimeTokenService,userDetailsService);
    }
    @Bean
    public OneTimeTokenService oneTimeTokenService(JdbcOperations jdbcOperations) {
        return new JdbcOneTimeTokenService(jdbcOperations);
    }
}

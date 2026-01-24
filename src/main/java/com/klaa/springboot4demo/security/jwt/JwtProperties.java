package com.klaa.springboot4demo.security.jwt;

import lombok.Data;
import lombok.NonNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Data
@Validated
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    @NonNull
    private Duration accessTokenExpiration = Duration.ofHours(1);
    @NonNull
    private Duration refreshTokenExpiration = Duration.ofDays(30);

}

package com.klaa.springboot4demo.security.token;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "security.token-pair")
public class TokenPairProperties {
    Duration ttl = Duration.ofMinutes(1);
}

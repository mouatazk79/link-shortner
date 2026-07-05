package com.klaa.springboot4demo.http.useragent;

import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static nl.basjes.parse.useragent.UserAgent.DEVICE_CLASS;
import static nl.basjes.parse.useragent.UserAgent.OPERATING_SYSTEM_NAME;

@Configuration
public class UserAgentAnalyzerConfig {
    private static final int cacheSize = 1000;

    @Bean
    public UserAgentAnalyzer userAgentAnalyzer(){
        return UserAgentAnalyzer
                .newBuilder()
                .withField(DEVICE_CLASS)
                .withField(OPERATING_SYSTEM_NAME)
                .withCache(cacheSize)
                .build();

    }
}

package com.klaa.springboot4demo.http.useragent;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Service;

import static nl.basjes.parse.useragent.UserAgent.DEVICE_CLASS;
import static nl.basjes.parse.useragent.UserAgent.OPERATING_SYSTEM_NAME;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserAgentAnalyzerService {
    UserAgentAnalyzer userAgentAnalyzer;

    public UserAgentInfo analyze(String userAgentHeader) {
        UserAgent userAgent = userAgentAnalyzer.parse(userAgentHeader == null ? "" : userAgentHeader);

        return UserAgentInfo.builder()
                .deviceClass(userAgent.getValue(DEVICE_CLASS))
                .operatingSystemName(userAgent.getValue(OPERATING_SYSTEM_NAME))
                .isBot(false)
                .isHacker(false)
                .build();
    }
}

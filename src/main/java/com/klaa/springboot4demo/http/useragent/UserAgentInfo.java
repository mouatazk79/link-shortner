package com.klaa.springboot4demo.http.useragent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAgentInfo {
    private String deviceClass;
    private String operatingSystemName;
    private boolean isBot;
    private boolean isHacker;
}

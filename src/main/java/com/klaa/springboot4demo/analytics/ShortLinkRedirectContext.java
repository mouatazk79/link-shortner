package com.klaa.springboot4demo.analytics;

import com.klaa.springboot4demo.http.useragent.UserAgentInfo;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpHeaders;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
public class ShortLinkRedirectContext implements Serializable {
    private UUID shortLinkId;
    private String remoteAddress;
    private int remotePort;
    private String requestURL;
    private String userAgent;
    private UserAgentInfo userAgentInfo;
    private String referer;
    private HttpHeaders headers;

}

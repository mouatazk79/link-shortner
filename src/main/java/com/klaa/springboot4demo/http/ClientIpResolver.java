package com.klaa.springboot4demo.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class ClientIpResolver {

    public String resolve(String remoteAddress, String requestURL, HttpHeaders headers) {
        // Check X-Forwarded-For header first (most common for proxies)
        String xForwardedFor = headers.getFirst("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            String clientIp = xForwardedFor.split(",")[0].trim();
            if (isValidIp(clientIp)) {
                return clientIp;
            }
        }

        // Check X-Real-IP header
        String xRealIp = headers.getFirst("X-Real-IP");
        if (StringUtils.hasText(xRealIp) && isValidIp(xRealIp)) {
            return xRealIp;
        }

        // Check Cloudflare header
        String cfConnectingIp = headers.getFirst("CF-Connecting-IP");
        if (StringUtils.hasText(cfConnectingIp) && isValidIp(cfConnectingIp)) {
            return cfConnectingIp;
        }

        // Fall back to remote address
        return remoteAddress != null ? remoteAddress : "UNKNOWN";
    }

    private boolean isValidIp(String ip) {
        return StringUtils.hasText(ip) && !ip.equalsIgnoreCase("unknown");
    }
}

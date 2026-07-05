package com.klaa.springboot4demo.analytics;

import com.klaa.springboot4demo.analytics.cache.RedisShortLinkRedirectAnalyticsService;
import com.klaa.springboot4demo.analytics.cache.ShortLinkRedirectAnalyticsCacheKey;
import com.klaa.springboot4demo.http.ClientIpResolver;
import com.klaa.springboot4demo.http.geoip.GeoIp2Service;
import com.klaa.springboot4demo.http.useragent.UserAgentAnalyzerService;
import com.klaa.springboot4demo.http.useragent.UserAgentInfo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ShortLinkRedirectAnalyticsService {

    UserAgentAnalyzerService userAgentAnalyzerService;
    GeoIp2Service geoLocationService;
    ClientIpResolver clientIpResolver;
    RedisShortLinkRedirectAnalyticsService redisShortLinkRedirectAnalyticsService;

    @Async
    public void recordRedirect(UUID shortLinkId,
                               String userAgent,
                               String referer,
                               HttpHeaders headers,
                               String remoteAddress,
                               String requestURL) {
        try {
            var userAgentInfo = userAgentAnalyzerService.analyze(userAgent);

            var context = ShortLinkRedirectContext
                    .builder()
                    .shortLinkId(shortLinkId)
                    .remoteAddress(remoteAddress)
                    .requestURL(requestURL)
                    .userAgent(userAgent)
                    .userAgentInfo(userAgentInfo)
                    .referer(referer)
                    .headers(headers)
                    .build();

            if (userAgentInfo.isBot()) {
                return;
            }
            if (userAgentInfo.isHacker()) {
                return;
            }
            doRecordRedirect(context, userAgentInfo);
        } catch (Exception e) {
            log.warn("Failed to record redirect analytics for short link {}", shortLinkId, e);
        }
    }

    private void doRecordRedirect(ShortLinkRedirectContext context, UserAgentInfo userAgentInfo) {
        var shortLinkId = context.getShortLinkId();
        var date = LocalDate.now();
        var country = analyticsValue(getCountryCode(context));
        var device = analyticsValue(userAgentInfo.getDeviceClass());
        var os = analyticsValue(userAgentInfo.getOperatingSystemName());
        var referer = analyticsValue(context.getReferer());

        var key = ShortLinkRedirectAnalyticsCacheKey
                .builder()
                .shortLinkId(shortLinkId)
                .date(date)
                .country(country)
                .device(device)
                .os(os)
                .referrer(referer)
                .build();

        redisShortLinkRedirectAnalyticsService.increment(key);

    }
    private String getCountryCode(ShortLinkRedirectContext context) {
        var clientIp = clientIpResolver.resolve(context.getRemoteAddress(), context.getRequestURL(), context.getHeaders());
        return geoLocationService.getCountryCodeOrUnknown(clientIp);
    }

    private String analyticsValue(String value) {
        return StringUtils.hasText(value) ? value : "UNKNOWN";
    }
}

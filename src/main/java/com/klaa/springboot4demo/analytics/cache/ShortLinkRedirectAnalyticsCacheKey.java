package com.klaa.springboot4demo.analytics.cache;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ShortLinkRedirectAnalyticsCacheKey {
    UUID shortLinkId;
    LocalDate date;
    String country;
    String device;
    String os;
    String referrer;
}

package com.klaa.springboot4demo.analytics;

import com.klaa.springboot4demo.analytics.cache.ShortLinkRedirectAnalyticsCacheKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ShortLinkRedirectAnalyticsRecordService {

    ShortLinkRedirectAnalyticsRecordRepository repository;

    @Transactional
    public void persistAnalytics(Map<ShortLinkRedirectAnalyticsCacheKey, Long> analyticsData) {
        analyticsData.forEach(this::persistSingleRecord);
    }

    private void persistSingleRecord(ShortLinkRedirectAnalyticsCacheKey key, Long count) {
        if (count <= 0) {
            return;
        }

        Optional<ShortLinkRedirectAnalyticsRecord> existingRecord = repository.findRecord(
                key.getShortLinkId(),
                key.getDate(),
                key.getCountry(),
                key.getDevice(),
                key.getOs(),
                key.getReferrer()
        );

        if (existingRecord.isPresent()) {
            repository.incrementCount(
                    existingRecord.get().getId(),
                    count,
                    Instant.now()
            );
        } else {
            ShortLinkRedirectAnalyticsRecord newRecord = ShortLinkRedirectAnalyticsRecord
                    .builder()
                    .shortLinkId(key.getShortLinkId())
                    .date(key.getDate())
                    .country(key.getCountry())
                    .device(key.getDevice())
                    .os(key.getOs())
                    .referrer(key.getReferrer())
                    .count(count)
                    .lastUpdatedTimestamp(Instant.now())
                    .build();

            repository.save(newRecord);
        }
    }
}

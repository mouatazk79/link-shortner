package com.klaa.springboot4demo.analytics;

import com.klaa.springboot4demo.analytics.cache.RedisShortLinkRedirectAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShortLinkRedirectAnalyticsFlushScheduler {

    private final RedisShortLinkRedirectAnalyticsService redisAnalyticsService;
    private final ShortLinkRedirectAnalyticsRecordService recordService;

    @Scheduled(fixedRate = 15000)
    public void flushAnalytics() {

        // 1. Recover any batches stranded by a previous failed run.
        //    Without this step, a single persistAnalytics() failure under
        //    load permanently strands that batch in Redis - it is never
        //    retried, which is the most likely reason for missing rows.
        Set<String> orphanedKeys = redisAnalyticsService.findOrphanedFlushKeys();
        if (orphanedKeys != null && !orphanedKeys.isEmpty()) {
            log.warn("Found {} orphaned analytics flush key(s), retrying", orphanedKeys.size());
            orphanedKeys.forEach(this::processFlushKey);
        }

        // 2. Swap the live hash and process the new batch as usual.
        String flushKey = redisAnalyticsService.swapAnalyticsHash();

        if (flushKey == null) {
            // Nothing was in the live hash, nothing to do this cycle.
            return;
        }

        processFlushKey(flushKey);
    }

    private void processFlushKey(String flushKey) {
        try {
            Map<?, Long> analytics = redisAnalyticsService.readAnalytics(flushKey);

            if (analytics.isEmpty()) {
                redisAnalyticsService.deleteAnalytics(flushKey);
                return;
            }

            recordService.persistAnalytics((Map) analytics);

            redisAnalyticsService.deleteAnalytics(flushKey);

            log.info("Persisted {} analytics records from {}", analytics.size(), flushKey);

        } catch (Exception ex) {

            log.error("Failed to persist analytics for key {}, will retry next cycle", flushKey, ex);

            // IMPORTANT:
            // Do NOT delete the temporary Redis key here.
            // findOrphanedFlushKeys() will pick it up and retry it
            // automatically on the next scheduled run.
        }
    }
}
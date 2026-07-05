package com.klaa.springboot4demo.analytics.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class RedisShortLinkRedirectAnalyticsService {

    private static final String ANALYTICS_KEY = "short-link:redirect:analytics";
    private static final String FLUSH_KEY_PREFIX = ANALYTICS_KEY + ":flush:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisShortLinkRedirectAnalyticsService(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper) {

        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void increment(ShortLinkRedirectAnalyticsCacheKey key) {
        redisTemplate.opsForHash().increment(
                ANALYTICS_KEY,
                serialize(key),
                1L
        );
    }

    /**
     * Atomically swaps the active hash with a temporary one.
     * Returns the new temporary key, or null if there was nothing to swap.
     */
    public String swapAnalyticsHash() {

        String flushKey = FLUSH_KEY_PREFIX + System.currentTimeMillis();

        Boolean swapped = redisTemplate.execute((RedisCallback<Boolean>) connection -> {

            byte[] source = ANALYTICS_KEY.getBytes(StandardCharsets.UTF_8);
            byte[] target = flushKey.getBytes(StandardCharsets.UTF_8);

            if (connection.exists(source)) {
                connection.rename(source, target);
                return true;
            }

            return false;
        });

        return Boolean.TRUE.equals(swapped) ? flushKey : null;
    }

    /**
     * Finds any temporary flush hashes left behind by a previous failed run.
     *
     * NOTE: uses KEYS, which blocks Redis on large keyspaces. Fine for a
     * low-cardinality prefix like this in dev/demo. For production at scale,
     * replace with a SCAN-based cursor implementation.
     */
    public Set<String> findOrphanedFlushKeys() {
        return redisTemplate.keys(FLUSH_KEY_PREFIX + "*");
    }

    public Map<ShortLinkRedirectAnalyticsCacheKey, Long> readAnalytics(String key) {

        Map<Object, Object> raw = redisTemplate.opsForHash().entries(key);

        Map<ShortLinkRedirectAnalyticsCacheKey, Long> result = new HashMap<>(raw.size());

        raw.forEach((field, value) ->
                result.put(
                        deserialize(field.toString()),
                        Long.parseLong(value.toString())
                ));

        return result;
    }

    public void deleteAnalytics(String key) {
        redisTemplate.delete(key);
    }

    private String serialize(ShortLinkRedirectAnalyticsCacheKey key) {
        try {
            return objectMapper.writeValueAsString(key);
        } catch (Exception e) {
            throw new IllegalStateException("Serialization failed", e);
        }
    }

    private ShortLinkRedirectAnalyticsCacheKey deserialize(String json) {
        try {
            return objectMapper.readValue(json, ShortLinkRedirectAnalyticsCacheKey.class);
        } catch (Exception e) {
            throw new IllegalStateException("Deserialization failed", e);
        }
    }
}
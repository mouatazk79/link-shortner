package com.klaa.springboot4demo.ratelimiting.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RateLimitingService {
    private static final int REQUEST_PER_MINUTE = 5;
    private final ProxyManager<String> proxyManager;


    public Bucket resolveBucket(String key) {
        Supplier<BucketConfiguration> configurationSupplier =this::getConfig;
        return proxyManager.builder()
                .build(key, configurationSupplier);
    }

    private BucketConfiguration getConfig() {
        var limit=Bandwidth.builder()
                .capacity(REQUEST_PER_MINUTE)
                .refillIntervally(
                        REQUEST_PER_MINUTE,
                       Duration.ofMinutes(1)).build();
        return BucketConfiguration.builder()
                .addLimit(limit).build();
    }


}

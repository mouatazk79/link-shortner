package com.klaa.springboot4demo.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShortLinkRedirectAnalyticsRecordRepository extends JpaRepository<ShortLinkRedirectAnalyticsRecord, UUID> {


    @Query("""
           SELECT a FROM ShortLinkRedirectAnalyticsRecord a
           WHERE a.shortLinkId = :shortLinkId
           AND a.date = :date
           AND a.country = :country
           AND a.device = :device
           AND a.os = :os
           AND a.referrer = :referrer
""")
    Optional<ShortLinkRedirectAnalyticsRecord> findRecord(
            @Param("shortLinkId") UUID shortLinkId,
            @Param("date")LocalDate date,
            @Param("country") String country,
            @Param("device") String device,
            @Param("os") String os,
            @Param("referrer") String referrer

            );

    @Modifying
    @Query("""
UPDATE ShortLinkRedirectAnalyticsRecord a
SET a.count = a.count + :increment,
    a.lastUpdatedTimestamp = :lastUpdatedTimestamp
WHERE a.id = :id
""")
    void incrementCount(
            @Param("id") UUID id,
            @Param("increment") long increment,
            @Param("lastUpdatedTimestamp")Instant instant
            );

    void deleteAllByShortLinkId(UUID ShortLinkId);
}

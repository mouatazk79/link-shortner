package com.klaa.springboot4demo.analytics;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ShortLinkRedirectAnalyticsRecords")
public class ShortLinkRedirectAnalyticsRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID shortLinkId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String device;

    @Column(nullable = false)
    private String os;

    @Column(nullable = false)
    private String referrer;

    @Column(nullable = false)
    @Builder.Default
    private Long count = 0L;

    @Column(nullable = false)
    private Instant lastUpdatedTimestamp;
}

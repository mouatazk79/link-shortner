package com.klaa.springboot4demo.shortlinks;

import com.klaa.springboot4demo.jpa.AbstractJpaVersionedAuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper=false)
@Table(name = "shortlinks")
public class ShortLink extends AbstractJpaVersionedAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="long_url",length = 2048)
    private String longUrl;

    @Column(name="short_url_key",length = 7,unique = true)
    private String shortUrlKey;

    @Transient
    private String shortUrl;
}

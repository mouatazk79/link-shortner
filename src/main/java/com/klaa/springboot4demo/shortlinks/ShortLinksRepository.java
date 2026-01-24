package com.klaa.springboot4demo.shortlinks;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShortLinksRepository extends JpaRepository<ShortLink, UUID> {
    Optional<ShortLink> getShortLinkByShortUrlKey(String shortUrlKey);
    Page<ShortLink> findAll(Pageable pageable);
}

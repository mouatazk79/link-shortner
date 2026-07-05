package com.klaa.springboot4demo.http.geoip;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GeoIp2Service {

    GeoIp2Repository repository;

    public String getCountryCodeOrUnknown(String ip) {
        return repository.getCountryCode(ip)
                .orElse("UNKNOWN");
    }
}

package com.klaa.springboot4demo.http.geoip;

import com.maxmind.geoip2.DatabaseProvider;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GeoIp2Repository {

    DatabaseProvider databaseProvider;

    public Optional<String> getCountryCode(String ip) {
        if (!StringUtils.hasText(ip) || ip.equalsIgnoreCase("UNKNOWN")) {
            return Optional.empty();
        }

        try {
            InetAddress address = InetAddress.getByName(ip);
            CountryResponse response = databaseProvider.country(address);
            String countryCode = response.representedCountry().isoCode();
            
            if (StringUtils.hasText(countryCode)) {
                return Optional.of(countryCode);
            }
        } catch (GeoIp2Exception e) {
            log.debug("GeoIP lookup failed for IP {}: {}", ip, e.getMessage());
        } catch (IOException e) {
            log.error("Error resolving IP {} to address", ip, e);
        } catch (Exception e) {
            log.error("Unexpected error during GeoIP lookup for IP {}", ip, e);
        }

        return Optional.empty();
    }
}

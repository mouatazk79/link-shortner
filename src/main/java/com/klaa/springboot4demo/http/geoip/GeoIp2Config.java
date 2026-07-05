package com.klaa.springboot4demo.http.geoip;

import com.maxmind.geoip2.DatabaseProvider;
import com.maxmind.geoip2.DatabaseReader;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class GeoIp2Config {
    @Value("classpath:geoip/GeoLite2-Country.mmdb")
    Resource geoIp2Database;

    @Bean
    @SneakyThrows
    public DatabaseProvider databaseProvider(){
        return new DatabaseReader.Builder(geoIp2Database.getInputStream()).build();
    }
}

package com.klaa.springboot4demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class Springboot4demoApplication {

    public static void main(String[] args) {
        SpringApplication.run(Springboot4demoApplication.class, args);
    }

}

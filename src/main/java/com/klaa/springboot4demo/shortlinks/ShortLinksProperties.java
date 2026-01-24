package com.klaa.springboot4demo.shortlinks;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "urlshortner.shortlinks")
public class ShortLinksProperties {
    @Min(2)
    @Max(7)
    private int shortUrlKeyLength;
}

package com.klaa.springboot4demo.shortlinks;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
public class ShortLinkDto {
    private String id;
    @URL
    private String longUrl;
    private String shortUrl;
}

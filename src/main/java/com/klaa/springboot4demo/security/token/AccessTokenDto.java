package com.klaa.springboot4demo.security.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Value
@Data
@AllArgsConstructor
public class AccessTokenDto {
    @JsonProperty("access_token")
    String access_token;
}

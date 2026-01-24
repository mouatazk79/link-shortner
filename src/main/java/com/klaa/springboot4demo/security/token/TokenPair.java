package com.klaa.springboot4demo.security.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class TokenPair {
    @JsonProperty("access_token")
    String access_token;
    @JsonProperty("refresh_token")
    String refresh_token;
}

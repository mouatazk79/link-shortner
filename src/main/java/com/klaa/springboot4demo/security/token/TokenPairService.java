package com.klaa.springboot4demo.security.token;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class TokenPairService {
    RedisTemplate<String, TokenPair> redisTemplate;
    TokenPairProperties tokenPairProperties;

    public String add(TokenPair tokenPair) {
        val  Code= UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(Code,tokenPair,tokenPairProperties.ttl);
        return Code;
    }
    public Optional<TokenPair> get(String code) {
        TokenPair tokenPair=redisTemplate.opsForValue().get(code);
        return Optional.ofNullable(tokenPair);
    }

    public void remove(String code) {
        redisTemplate.delete(code);
    }

}

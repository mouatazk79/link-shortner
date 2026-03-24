package com.klaa.springboot4demo.security.token;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.klaa.springboot4demo.security.token.RefreshTokenAuthenticationConverter.REFRESH_TOKEN_COOKIE;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenController {
    TokenService tokenService;

    @PostMapping("/refresh")
    public AccessTokenDto refresh(@CookieValue(name = REFRESH_TOKEN_COOKIE) String refreshToken) {
        val accessToken=tokenService.refreshToken(refreshToken);
        return new AccessTokenDto(accessToken);
    }
}

package com.klaa.springboot4demo.security.token;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenAuthenticationConverter implements AuthenticationConverter {
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    @Override
    public @Nullable Authentication convert(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (token != null && !token.isEmpty()) {
                        return new RefreshTokenAuthenticationToken(token);
                    }
                }
            }
        }
        return null;
    }
}

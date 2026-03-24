package com.klaa.springboot4demo.security.token;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.Collections;


@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {
    JwtDecoder jwtDecoder;
    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        val token = (String) authentication.getCredentials();
        try {
            val jwt = jwtDecoder.decode(token);
            val scopeClaim = jwt.getClaimAsString("scope");
            // todo we use the scopeClaim using an if

            return new RefreshTokenAuthenticationToken(jwt, Collections.emptyList());
        } catch (Exception e) {
            throw new BadCredentialsException("Refresh token authentication failed", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RefreshTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

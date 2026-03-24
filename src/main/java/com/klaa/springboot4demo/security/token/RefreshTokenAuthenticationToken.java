package com.klaa.springboot4demo.security.token;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class RefreshTokenAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private Object credentials;

    public RefreshTokenAuthenticationToken(String token) {
        super(Collections.emptyList());
        this.principal = null;
        this.credentials = token;
        this.setAuthenticated(false);
    }

    public RefreshTokenAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = null;
        this.setAuthenticated(true);
    }

    @Override
    public @Nullable Object getCredentials() {
        return this.credentials;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }
}

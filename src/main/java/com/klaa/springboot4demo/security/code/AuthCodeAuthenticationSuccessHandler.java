package com.klaa.springboot4demo.security.code;

import com.klaa.springboot4demo.security.refreshtoken.RefreshTokenService;
import com.klaa.springboot4demo.security.token.TokenPair;
import com.klaa.springboot4demo.security.token.TokenService;
import com.klaa.springboot4demo.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public abstract class AuthCodeAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    RefreshTokenService refreshTokenService;
    TokenService tokenService;

    protected TokenPair generateTokenPair(User user) {
        val accessToken = tokenService.generateAccessToken(user);
        val refreshToken = tokenService.generateRefreshToken(user);
        saveRefreshToken(user,refreshToken);
        return new TokenPair(accessToken,refreshToken);
    }

    private void saveRefreshToken(User user, String tokenValue) {
        refreshTokenService.create(user.getId(), tokenValue);
    }

    @SneakyThrows
    protected void sendAuthCodeRedirect(HttpServletRequest request, HttpServletResponse response,String code) {
        val redirectStrategy=getRedirectStrategy();
        val frontEndUrl="http://localhost:4200/";
        val builder=fromUriString(frontEndUrl);
        builder.path("code/exchange");
        builder.queryParam("code",code);
        val codeExchangeUrl=builder.toUriString();
        redirectStrategy.sendRedirect(request,response,codeExchangeUrl);
    }





}

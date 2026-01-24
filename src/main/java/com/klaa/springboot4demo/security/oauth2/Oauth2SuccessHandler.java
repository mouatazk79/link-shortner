package com.klaa.springboot4demo.security.oauth2;

import com.klaa.springboot4demo.security.code.AuthCodeAuthenticationSuccessHandler;
import com.klaa.springboot4demo.security.refreshtoken.RefreshTokenService;
import com.klaa.springboot4demo.security.token.TokenPairService;
import com.klaa.springboot4demo.security.token.TokenService;
import com.klaa.springboot4demo.user.User;
import com.klaa.springboot4demo.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class Oauth2SuccessHandler extends AuthCodeAuthenticationSuccessHandler {
    private static final String EMAIL="email";
    TokenPairService tokenPairService;
    UserService userService;

    public Oauth2SuccessHandler(RefreshTokenService refreshTokenService, TokenService tokenService, TokenPairService tokenPairService, UserService userService) {
        super(refreshTokenService, tokenService);
        this.tokenPairService = tokenPairService;
        this.userService = userService;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if(authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken){
            handOauth2Authentication(request,response,oAuth2AuthenticationToken);
            return;
        }
        super.onAuthenticationSuccess(request,response,authentication);
    }

    @SneakyThrows
    private void handOauth2Authentication(HttpServletRequest request, HttpServletResponse response, OAuth2AuthenticationToken authentication) {
        val principle = authentication.getPrincipal();
        val email=(String) principle.getAttribute(EMAIL);
        val user=userService.findByEmail(email).orElseGet(()->saveUser(email));
        val tokenPair=generateTokenPair(user);
        val code=tokenPairService.add(tokenPair);
        sendAuthCodeRedirect(request,response,code);
    }
    private User saveUser(String email){
       return   userService.saveUser(email);
    }
}

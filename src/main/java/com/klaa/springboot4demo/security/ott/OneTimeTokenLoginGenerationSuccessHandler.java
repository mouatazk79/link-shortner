package com.klaa.springboot4demo.security.ott;

import com.klaa.springboot4demo.security.code.AuthCodeAuthenticationSuccessHandler;
import com.klaa.springboot4demo.security.refreshtoken.RefreshTokenService;
import com.klaa.springboot4demo.security.token.TokenPairService;
import com.klaa.springboot4demo.security.token.TokenService;
import com.klaa.springboot4demo.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.security.authentication.ott.OneTimeTokenAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class OneTimeTokenLoginGenerationSuccessHandler extends AuthCodeAuthenticationSuccessHandler {
    UserService userService;
    TokenPairService tokenPairService;

    public OneTimeTokenLoginGenerationSuccessHandler(RefreshTokenService refreshTokenService, TokenService tokenService, UserService userService, TokenPairService tokenPairService) {
        super(refreshTokenService, tokenService);
        this.userService = userService;
        this.tokenPairService = tokenPairService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,Authentication authentication) throws IOException, ServletException {
        System.out.println(authentication);
        if (authentication instanceof OneTimeTokenAuthentication oneTimeTokenAuthenticationToken){
            handleOneTimeTokenAuthenticationToken(request,response,oneTimeTokenAuthenticationToken);
            return;

        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private void handleOneTimeTokenAuthenticationToken(HttpServletRequest request, HttpServletResponse response, OneTimeTokenAuthentication oneTimeTokenAuthentication) {
        val email=oneTimeTokenAuthentication.getName();
        val user=userService.findByEmail(email);
        val tokenPair=generateTokenPair(user.get());
        val code=tokenPairService.add(tokenPair);
        sendAuthCodeRedirect(request,response,code);
    }
}

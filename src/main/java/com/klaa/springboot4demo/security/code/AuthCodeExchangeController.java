package com.klaa.springboot4demo.security.code;

import com.klaa.springboot4demo.security.jwt.JwtProperties;
import com.klaa.springboot4demo.security.token.AccessTokenDto;
import com.klaa.springboot4demo.security.token.TokenPairService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.klaa.springboot4demo.security.token.RefreshTokenAuthenticationConverter.REFRESH_TOKEN_COOKIE;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthCodeExchangeController {
    TokenPairService tokenPairService;
    JwtProperties jwtProperties;
    @GetMapping("/code/exchange")
    public ResponseEntity<AccessTokenDto> getCode(@RequestParam String code) {
        val tokenPair=tokenPairService.get(code);
        if (tokenPair.isEmpty()){
            throw new RuntimeException();
        }
        tokenPairService.remove(code);
        val refreshTokenCookie=buildRefreshTokenCookie(tokenPair.get().getRefresh_token());
        val accessToken=new AccessTokenDto(tokenPair.get().getAccess_token());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie)
                .body(accessToken);
    }
    private String buildRefreshTokenCookie(String refreshToken) {
        val age=jwtProperties.getRefreshTokenExpiration();
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE,refreshToken)
                .maxAge(age)
                .secure(false)
                .httpOnly(true)
                .sameSite("Strict")
                .path("/")
                .build()
                .toString();
    }
}

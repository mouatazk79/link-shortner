package com.klaa.springboot4demo.security.token;

import com.klaa.springboot4demo.security.jwt.JwtService;
import com.klaa.springboot4demo.user.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenService {
    JwtService jwtService;
    public String generateAccessToken(User user){
        return jwtService.generateAccessToken(user);
    }
    public String generateRefreshToken(User user){
        return jwtService.generateRefreshToken(user);
    }

    public String refreshToken(String refreshToken) {
        return jwtService.refreshToken(refreshToken);
    }
}

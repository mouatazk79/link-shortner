package com.klaa.springboot4demo.security.jwt;

import com.klaa.springboot4demo.user.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

import static org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256;
import static org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS512;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtService {
    JwtEncoder jwtEncoder;
    JwtProperties jwtProperties;

    public String generateAccessToken(User user){
        val ttl=jwtProperties.getAccessTokenExpiration();
        return generateToken(user,ttl);
    }
    public String generateRefreshToken(User user){
        val ttl=jwtProperties.getRefreshTokenExpiration();
        return generateToken(user,ttl);
    }

    private String generateToken(User user, Duration ttl) {
        val builder= JwtClaimsSet.builder()
                .issuer("slef");
        val now= Instant.now();
        builder.issuedAt(now);
        val expiresAt= now.plus(ttl);
        val userId= user.getId().toString();
        builder.subject(userId);
        builder.expiresAt(expiresAt);
        val claims=builder.build();
        val jwsHeader= JwsHeader.with(HS256).build();
        val parameters= JwtEncoderParameters.from(jwsHeader,claims);
        return jwtEncoder.encode(parameters).getTokenValue();
    }


}

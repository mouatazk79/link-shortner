package com.klaa.springboot4demo.security.jwt;

import com.klaa.springboot4demo.user.User;
import com.klaa.springboot4demo.user.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtService {
    JwtEncoder jwtEncoder;
    JwtDecoder jwtDecoder;
    UserRepository userRepository;
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
    public String refreshToken(String refreshToken) {
        try {
            val jwt = jwtDecoder.decode(refreshToken);

            val userId = jwt.getSubject();

            val expiresAt = jwt.getExpiresAt();
            if (expiresAt != null && expiresAt.isBefore(Instant.now())) {
                throw new RuntimeException("Refresh token has expired");
            }

            // 4. Optional: Check if this is actually a refresh token (not an access token)
            // You might want to add a "type" claim to differentiate
            val tokenType = jwt.getClaimAsString("type");
            if (tokenType != null && !"refresh".equals(tokenType)) {
//                throw new InvalidTokenException("Token is not a refresh token");
            }

            // 5. Fetch the user from the database
            val user = userRepository.findById(UUID.fromString(userId))
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));


            // 7. Generate and return a new access token
            return generateAccessToken(user);

        } catch (JwtException e) {
            // Handle JWT-specific exceptions (expired, invalid signature, etc.)
            throw new RuntimeException("Invalid refresh token: " + e.getMessage(), e);
        }
    }


}

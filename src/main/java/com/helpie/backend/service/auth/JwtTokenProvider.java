package com.helpie.backend.service.auth;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpie.backend.dto.global.IJwtClaim;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;

    @Autowired
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(IJwtClaim jwtClaim, long expiredMs) {
        return Jwts
                .builder()
                .signWith(this.secretKey)
                .claims(jwtClaim.toMap())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + expiredMs))
                .compact();
    }

    public String createToken(IJwtClaim jwtClaim) {
        return this.createToken(jwtClaim, Duration.ofHours(1).toMillis());
    }

    public <T extends IJwtClaim> T decodeJwt(String jwtToken, Class<T> clz) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        final Claims claims = Jwts.parser()
                .verifyWith(this.secretKey)
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();

        return objectMapper.convertValue(claims, clz);
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jwts.parser()
                    .verifyWith(this.secretKey)
                    .build()
                    .parseSignedClaims(jwtToken);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
package com.mealtracker.security.jwt;

import com.mealtracker.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;

@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;

    public JwtTokenProvider(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public String generateToken(Authentication authentication) {
        var userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return Jwts.builder()
                .claims().add(userPrincipal.toJwtClaims()).and()
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public Claims getBodyFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

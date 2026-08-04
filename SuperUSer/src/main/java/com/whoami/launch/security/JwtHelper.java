package com.whoami.launch.security;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtHelper {

    private static final String SECRET =
            "SuperUserAdminSecretKeyMustBeAtLeast256BitsLongForJwtSigning123";

    private static final long ACCESS_TOKEN_VALIDITY =
            1000 * 60 * 60;

    private static final long REFRESH_TOKEN_VALIDITY =
            1000L * 60 * 60 * 24 * 7;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
                SECRET.getBytes());
    }

    public String generateToken(
            String adminId,
            String email) {

        return Jwts.builder()
                .subject(email)
                .claim("adminId", adminId)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + ACCESS_TOKEN_VALIDITY))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(
            String adminId,
            String email) {

        return Jwts.builder()
                .subject(email)
                .claim("adminId", adminId)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + REFRESH_TOKEN_VALIDITY))
                .signWith(getSigningKey())
                .compact();
    }

    public String getEmailFromToken(
            String token) {

        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenExpired(
            String token) {

        Date expiry =
                Jwts.parser()
                        .verifyWith((SecretKey) getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getExpiration();

        return expiry.before(new Date());
    }
}
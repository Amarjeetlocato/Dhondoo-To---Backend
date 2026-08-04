package com.whoami.launch.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtHelper {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /*
     * ===============================
     * TOKEN GENERATION
     * ===============================
     * 
     * 
     */
    public String generateToken(
            String userId,
            String email
    ) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", userId);
        claims.put("email", email);

        return createToken(claims, email);
    }

    private String createToken(
            Map<String, Object> claims,
            String subject
    ) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // email
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + jwtExpiration)
                )
                .signWith(getSigningKey())
                .compact();
    }

    /*
     * ===============================
     * CLAIM EXTRACTION
     * ===============================
     */
    public <T> T getClaimFromToken(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        Claims claims = getAllClaims(token);

        return claimsResolver.apply(claims);
    }

    private Claims getAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /*
     * ===============================
     * STANDARD CLAIMS
     * ===============================
     */
    public String getUsernameFromToken(String token) {

        return getClaimFromToken(
                token,
                Claims::getSubject
        );
    }

    public Date getExpirationDate(String token) {

        return getClaimFromToken(
                token,
                Claims::getExpiration
        );
    }

    /*
     * ===============================
     * CUSTOM CLAIMS
     * ===============================
     */
    public String getUserIdFromToken(String token) {

        return getClaimFromToken(
                token,
                claims -> claims.get("userId", String.class)
        );
    }

    public String getEmailFromToken(String token) {

        return getClaimFromToken(
                token,
                claims -> claims.get("email", String.class)
        );
    }

    public String getRoleFromToken(String token) {

        return getClaimFromToken(
                token,
                claims -> claims.get("role", String.class)
        );
    }

    /*
     * ===============================
     * TOKEN VALIDATION
     * ===============================
     */
    public Boolean isTokenExpired(String token) {

        return getExpirationDate(token)
                .before(new Date());
    }

    public Boolean validateToken(
            String token,
            String email
    ) {

        final String tokenEmail =
                getEmailFromToken(token);

        return tokenEmail.equals(email)
                && !isTokenExpired(token);
    }
    
    public String generateRefreshToken(
            String userId,
            String email
    ) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", userId);
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + refreshExpiration
                        )
                )
                .signWith(getSigningKey())
                .compact();
    }
}
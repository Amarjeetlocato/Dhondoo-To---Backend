package com.whoami.launch.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final String ADMIN_SECRET =
            "SuperUserAdminSecretKeyMustBeAtLeast256BitsLongForJwtSigning123";

    private static final String USER_SECRET =
            "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

    private Key getAdminKey() {
        return Keys.hmacShaKeyFor(
                ADMIN_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private Key getUserKey() {
        return Keys.hmacShaKeyFor(
                USER_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Try Admin Token first.
     * If fails then try User Token.
     */
    public Claims extractClaims(String token) {

        try {

            Claims claims =
                    Jwts.parserBuilder()
                            .setSigningKey(getAdminKey())
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

            System.out.println("ADMIN TOKEN VALID");
            return claims;

        } catch (Exception ignored) {

            try {

                Claims claims =
                        Jwts.parserBuilder()
                                .setSigningKey(getUserKey())
                                .build()
                                .parseClaimsJws(token)
                                .getBody();

                System.out.println("USER TOKEN VALID");
                return claims;

            } catch (Exception ex) {

                System.out.println("TOKEN INVALID");
                throw new JwtException(
                        "Invalid JWT Token",
                        ex
                );
            }
        }
    }

    public boolean validateToken(String token) {

        try {

            extractClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    public String extractUsername(String token) {

        return extractClaims(token)
                .getSubject();
    }

    public String extractEmail(String token) {

        Claims claims = extractClaims(token);

        return claims.get(
                "email",
                String.class
        );
    }

    public String extractUserId(String token) {

        Claims claims = extractClaims(token);

        return claims.get(
                "userId",
                String.class
        );
    }

    public String extractAdminId(String token) {

        Claims claims = extractClaims(token);

        return claims.get(
                "adminId",
                String.class
        );
    }

    public boolean isAdminToken(String token) {

        try {

            Claims claims = extractClaims(token);

            return claims.get("adminId") != null;

        } catch (Exception e) {

            return false;
        }
    }

    public boolean isUserToken(String token) {

        try {

            Claims claims = extractClaims(token);

            return claims.get("userId") != null;

        } catch (Exception e) {

            return false;
        }
    }

    public boolean isTokenExpired(String token) {

        try {

            Date expiration =
                    extractClaims(token)
                            .getExpiration();

            return expiration.before(
                    new Date());

        } catch (Exception e) {

            return true;
        }
    }
}
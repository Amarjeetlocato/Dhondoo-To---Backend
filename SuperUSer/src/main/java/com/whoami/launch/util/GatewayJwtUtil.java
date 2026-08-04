/*
 * package com.whoami.launch.util;
 * 
 * import java.nio.charset.StandardCharsets; import java.security.Key;
 * 
 * import org.springframework.stereotype.Component;
 * 
 * import io.jsonwebtoken.Claims; import io.jsonwebtoken.JwtException; import
 * io.jsonwebtoken.Jwts; import io.jsonwebtoken.security.Keys;
 * 
 *//**
	 * Gateway JWT Utility - Validates tokens from SuperUser service Uses the same
	 * SECRET key as SuperUser service for token verification
	 */
/*
 * @Component public class GatewayJwtUtil {
 * 
 * // MUST MATCH the SECRET in SuperUser service private static final String
 * SECRET = "SuperUserAdminSecretKeyMustBeAtLeast256BitsLongForJwtSigning123";
 * 
 * private Key getKey() { return Keys.hmacShaKeyFor(
 * SECRET.getBytes(StandardCharsets.UTF_8) ); }
 * 
 *//**
	 * Extract claims from JWT token
	 * 
	 * @param token - JWT token string
	 * @return Claims object containing token data
	 * @throws JwtException if token is invalid
	 */
/*
 * public Claims extractClaims(String token) { try { Claims claims =
 * Jwts.parserBuilder() .setSigningKey(getKey()) .build() .parseClaimsJws(token)
 * .getBody();
 * 
 * System.out.println("[GATEWAY] TOKEN VALID - Subject: " +
 * claims.getSubject()); return claims; } catch (Exception e) {
 * System.out.println("[GATEWAY] TOKEN EXTRACTION FAILED: " + e.getMessage());
 * throw new JwtException("Invalid token", e); } }
 * 
 *//**
	 * Validate JWT token signature and expiration
	 * 
	 * @param token - JWT token string
	 * @return true if token is valid, false otherwise
	 */
/*
 * public boolean validateToken(String token) { try { if (token == null ||
 * token.trim().isEmpty()) { return false; }
 * 
 * extractClaims(token);
 * System.out.println("[GATEWAY] TOKEN VALIDATION SUCCESSFUL"); return true;
 * 
 * } catch (JwtException | IllegalArgumentException e) {
 * System.out.println("[GATEWAY] TOKEN VALIDATION FAILED: " + e.getMessage());
 * return false; } catch (Exception e) {
 * System.out.println("[GATEWAY] UNEXPECTED ERROR: " + e.getClass().getName());
 * e.printStackTrace(); return false; } }
 * 
 *//**
	 * Extract username/subject from token
	 * 
	 * @param token - JWT token string
	 * @return username from token subject claim
	 */
/*
 * public String extractUsername(String token) { try { return
 * extractClaims(token).getSubject(); } catch (Exception e) {
 * System.out.println("[GATEWAY] FAILED TO EXTRACT USERNAME: " +
 * e.getMessage()); return null; } }
 * 
 *//**
	 * Extract specific claim from token
	 * 
	 * @param token     - JWT token string
	 * @param claimName - name of the claim to extract
	 * @return claim value as Object
	 */
/*
 * public Object extractClaim(String token, String claimName) { try { Claims
 * claims = extractClaims(token); return claims.get(claimName); } catch
 * (Exception e) { System.out.println("[GATEWAY] FAILED TO EXTRACT CLAIM " +
 * claimName + ": " + e.getMessage()); return null; } }
 * 
 *//**
	 * Check if token is expired
	 * 
	 * @param token - JWT token string
	 * @return true if expired, false otherwise
	 */
/*
 * public boolean isTokenExpired(String token) { try { Claims claims =
 * extractClaims(token); return claims.getExpiration().before(new
 * java.util.Date()); } catch (Exception e) { return true; } }
 * 
 *//**
	 * Get token expiration time
	 * 
	 * @param token - JWT token string
	 * @return expiration date
	 *//*
		 * public java.util.Date getExpirationTime(String token) { try { return
		 * extractClaims(token).getExpiration(); } catch (Exception e) { return null; }
		 * } }
		 */
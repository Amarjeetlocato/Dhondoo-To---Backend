/*
 * package com.whoami.launch.security;
 * 
 * import java.util.Date; import java.util.HashMap; import java.util.Map;
 * 
 * import javax.crypto.SecretKey;
 * 
 * import org.springframework.stereotype.Component;
 * 
 * import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import
 * lombok.extern.slf4j.Slf4j;
 * 
 * @Component
 * 
 * @Slf4j public class GatewayTokenValidator {
 * 
 * private static final String SECRET =
 * "SuperUserAdminSecretKeyMustBeAtLeast256BitsLongForJwtSigning123";
 * 
 * private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60;
 * 
 *//**
	 * Validate JWT token and extract claims
	 */
/*
 * public TokenValidationResult validateToken(String token) {
 * 
 * try { if (token == null || token.isEmpty()) { return
 * TokenValidationResult.invalid("Token is null or empty"); }
 * 
 * Claims claims = Jwts.parser() .verifyWith((SecretKey) getSigningKey())
 * .build() .parseSignedClaims(token) .getPayload();
 * 
 * if (isTokenExpired(claims)) { return
 * TokenValidationResult.invalid("Token has expired"); }
 * 
 * String email = claims.getSubject(); String adminId = (String)
 * claims.get("adminId");
 * 
 * log.info("Token validated successfully for: {}", email);
 * 
 * return TokenValidationResult.valid(email, adminId, claims);
 * 
 * } catch (ExpiredJwtException ex) { log.warn("Token expired: {}",
 * ex.getMessage()); return TokenValidationResult.invalid("Token expired");
 * 
 * } catch (UnsupportedJwtException ex) { log.warn("Unsupported JWT: {}",
 * ex.getMessage()); return
 * TokenValidationResult.invalid("Unsupported token format");
 * 
 * } catch (MalformedJwtException ex) { log.warn("Malformed JWT: {}",
 * ex.getMessage()); return
 * TokenValidationResult.invalid("Invalid token format");
 * 
 * } catch (SignatureException ex) { log.warn("Invalid signature: {}",
 * ex.getMessage()); return
 * TokenValidationResult.invalid("Invalid token signature");
 * 
 * } catch (IllegalArgumentException ex) { log.warn("Invalid claims: {}",
 * ex.getMessage()); return
 * TokenValidationResult.invalid("Invalid token claims");
 * 
 * } catch (Exception ex) { log.error("Token validation error: {}",
 * ex.getMessage()); return
 * TokenValidationResult.invalid("Token validation failed"); } }
 * 
 *//**
	 * Extract token from Authorization header
	 */
/*
 * public String extractTokenFromHeader(String authorizationHeader) {
 * 
 * if (authorizationHeader == null ||
 * !authorizationHeader.startsWith("Bearer ")) { return null; }
 * 
 * return authorizationHeader.substring(7); }
 * 
 *//**
	 * Validate token without parsing claims
	 */
/*
 * public boolean isTokenValid(String token) { return
 * validateToken(token).isValid(); }
 * 
 *//**
	 * Get email from token
	 */
/*
 * public String getEmailFromToken(String token) { try { return Jwts.parser()
 * .verifyWith((SecretKey) getSigningKey()) .build() .parseSignedClaims(token)
 * .getPayload() .getSubject(); } catch (Exception ex) {
 * log.error("Failed to extract email from token: {}", ex.getMessage()); return
 * null; } }
 * 
 *//**
	 * Get admin ID from token
	 */
/*
 * public String getAdminIdFromToken(String token) { try { Object adminId =
 * Jwts.parser() .verifyWith((SecretKey) getSigningKey()) .build()
 * .parseSignedClaims(token) .getPayload() .get("adminId");
 * 
 * return adminId != null ? adminId.toString() : null; } catch (Exception ex) {
 * log.error("Failed to extract adminId from token: {}", ex.getMessage());
 * return null; } }
 * 
 *//**
	 * Check if token is expired
	 */
/*
 * private boolean isTokenExpired(Claims claims) { Date expiration =
 * claims.getExpiration(); return expiration != null && expiration.before(new
 * Date()); }
 * 
 *//**
	 * Get signing key
	 */
/*
 * private javax.crypto.Key getSigningKey() { return
 * Keys.hmacShaKeyFor(SECRET.getBytes()); }
 * 
 *//**
	 * Token Validation Result DTO
	 *//*
		 * public static class TokenValidationResult {
		 * 
		 * private final boolean valid; private final String message; private final
		 * String email; private final String adminId; private final Claims claims;
		 * 
		 * private TokenValidationResult( boolean valid, String message, String email,
		 * String adminId, Claims claims) { this.valid = valid; this.message = message;
		 * this.email = email; this.adminId = adminId; this.claims = claims; }
		 * 
		 * public static TokenValidationResult valid( String email, String adminId,
		 * Claims claims) { return new TokenValidationResult( true, "Token is valid",
		 * email, adminId, claims); }
		 * 
		 * public static TokenValidationResult invalid(String message) { return new
		 * TokenValidationResult( false, message, null, null, null); }
		 * 
		 * public boolean isValid() { return valid; }
		 * 
		 * public String getMessage() { return message; }
		 * 
		 * public String getEmail() { return email; }
		 * 
		 * public String getAdminId() { return adminId; }
		 * 
		 * public Claims getClaims() { return claims; }
		 * 
		 * public Map<String, Object> toMap() { Map<String, Object> result = new
		 * HashMap<>(); result.put("valid", valid); result.put("message", message);
		 * result.put("email", email); result.put("adminId", adminId); return result; }
		 * } }
		 */
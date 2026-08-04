/*
 * package com.whoami.launch.filter;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.cloud.gateway.filter.GatewayFilter; import
 * org.springframework.cloud.gateway.filter.factory.
 * AbstractGatewayFilterFactory; import org.springframework.http.HttpStatus;
 * import org.springframework.http.server.reactive.ServerHttpRequest; import
 * org.springframework.http.server.reactive.ServerHttpResponse; import
 * org.springframework.stereotype.Component; import
 * org.springframework.web.server.ServerWebExchange;
 * 
 * import com.whoami.launch.util.GatewayJwtUtil;
 * 
 * import io.jsonwebtoken.Claims; import reactor.core.publisher.Mono;
 * 
 *//**
	 * JWT Authentication Filter for API Gateway Validates JWT tokens before routing
	 * to backend services
	 */
/*
 * @Component public class JwtAuthenticationFilter extends
 * AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
 * 
 * @Autowired private GatewayJwtUtil jwtUtil;
 * 
 * public static class Config { // Can add configuration properties here if
 * needed }
 * 
 * public JwtAuthenticationFilter() { super(Config.class); }
 * 
 * @Override public GatewayFilter apply(Config config) { return (exchange,
 * chain) -> { ServerHttpRequest request = exchange.getRequest();
 * 
 * // Skip token validation for public endpoints if
 * (isPublicEndpoint(request.getPath().toString())) { return
 * chain.filter(exchange); }
 * 
 * System.out.println("[GATEWAY FILTER] Processing request: " +
 * request.getPath());
 * 
 * // Extract token from Authorization header String token =
 * extractToken(request);
 * 
 * if (token == null || token.isEmpty()) {
 * System.out.println("[GATEWAY FILTER] No token found in request"); return
 * onError(exchange, "Missing authorization token", HttpStatus.UNAUTHORIZED); }
 * 
 * // Validate token if (!jwtUtil.validateToken(token)) {
 * System.out.println("[GATEWAY FILTER] Invalid token"); return
 * onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED); }
 * 
 * try { // Extract claims Claims claims = jwtUtil.extractClaims(token); String
 * username = jwtUtil.extractUsername(token);
 * 
 * System.out.println("[GATEWAY FILTER] Token valid for user: " + username);
 * 
 * // Add headers to pass to backend services ServerHttpRequest modifiedRequest
 * = request.mutate() .header("X-User", username) .header("X-Authenticated",
 * "true") .header("X-Token-Valid", "true") .build();
 * 
 * ServerWebExchange modifiedExchange = exchange.mutate()
 * .request(modifiedRequest) .build();
 * 
 * return chain.filter(modifiedExchange);
 * 
 * } catch (Exception e) {
 * System.out.println("[GATEWAY FILTER] Error processing token: " +
 * e.getMessage()); return onError(exchange, "Token processing error",
 * HttpStatus.UNAUTHORIZED); } }; }
 * 
 *//**
	 * Extract JWT token from Authorization header Expected format: "Bearer <token>"
	 */
/*
 * private String extractToken(ServerHttpRequest request) { String authorization
 * = request.getHeaders().getFirst("Authorization");
 * 
 * if (authorization != null && authorization.startsWith("Bearer ")) { return
 * authorization.substring(7); }
 * 
 * return null; }
 * 
 *//**
	 * Check if endpoint is public (no token validation needed)
	 */
/*
 * private boolean isPublicEndpoint(String path) { // Add public endpoints here
 * that don't require authentication return path.contains("/auth/login") ||
 * path.contains("/auth/register") || path.contains("/public") ||
 * path.contains("/health") || path.contains("/swagger") ||
 * path.contains("/api-docs"); }
 * 
 *//**
	 * Handle authentication error
	 *//*
		 * private Mono<Void> onError(ServerWebExchange exchange, String message,
		 * HttpStatus status) { ServerHttpResponse response = exchange.getResponse();
		 * response.setStatusCode(status); response.getHeaders().add("Content-Type",
		 * "application/json");
		 * 
		 * String errorResponse = String.format(
		 * "{\"error\": \"%s\", \"status\": %d, \"timestamp\": \"%s\"}", message,
		 * status.value(), java.time.Instant.now() );
		 * 
		 * return response.writeString(Mono.just(errorResponse)); } }
		 */
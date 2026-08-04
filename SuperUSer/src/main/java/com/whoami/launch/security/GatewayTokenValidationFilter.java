/*
 * package com.whoami.launch.security;
 * 
 * import java.nio.charset.StandardCharsets;
 * 
 * import org.springframework.cloud.gateway.filter.GatewayFilter; import
 * org.springframework.cloud.gateway.filter.factory.
 * AbstractGatewayFilterFactory; import org.springframework.http.HttpStatus;
 * import org.springframework.http.server.reactive.ServerHttpRequest; import
 * org.springframework.stereotype.Component; import
 * org.springframework.web.server.ServerWebExchange;
 * 
 * import com.fasterxml.jackson.databind.ObjectMapper; import
 * com.whoami.launch.dto.ApiResponse;
 * 
 * import lombok.extern.slf4j.Slf4j; import reactor.core.publisher.Mono;
 * 
 *//**
	 * Gateway Filter for JWT Token Validation Use this in Spring Cloud Gateway to
	 * validate tokens on all requests
	 */
/*
 * @Component
 * 
 * @Slf4j public class GatewayTokenValidationFilter extends
 * AbstractGatewayFilterFactory<GatewayTokenValidationFilter.Config> {
 * 
 * private final GatewayTokenValidator tokenValidator; private final
 * ObjectMapper objectMapper;
 * 
 * public GatewayTokenValidationFilter( GatewayTokenValidator tokenValidator) {
 * super(Config.class); this.tokenValidator = tokenValidator; this.objectMapper
 * = new ObjectMapper(); }
 * 
 * @Override public GatewayFilter apply(Config config) {
 * 
 * return (exchange, chain) -> {
 * 
 * ServerHttpRequest request = exchange.getRequest(); String path =
 * request.getPath().value();
 * 
 * // Public paths - skip validation if (isPublicPath(path)) { return
 * chain.filter(exchange); }
 * 
 * // Extract authorization header String authHeader = request.getHeaders()
 * .getFirst("Authorization");
 * 
 * if (authHeader == null || authHeader.isEmpty()) {
 * log.warn("Missing Authorization header for path: {}", path); return
 * unauthorizedResponse( exchange, "Missing Authorization header"); }
 * 
 * // Extract token from header String token = tokenValidator
 * .extractTokenFromHeader(authHeader);
 * 
 * if (token == null) {
 * log.warn("Invalid Authorization header format for path: {}", path); return
 * unauthorizedResponse( exchange, "Invalid Authorization header format"); }
 * 
 * // Validate token GatewayTokenValidator.TokenValidationResult result =
 * tokenValidator.validateToken(token);
 * 
 * if (!result.isValid()) { log.warn("Token validation failed: {} for path: {}",
 * result.getMessage(), path); return unauthorizedResponse(exchange,
 * result.getMessage()); }
 * 
 * log.info("Token validated for user: {} on path: {}", result.getEmail(),
 * path);
 * 
 * // Add user info to headers for downstream services ServerHttpRequest
 * mutatedRequest = request.mutate() .header("X-User-Email", result.getEmail())
 * .header("X-Admin-Id", result.getAdminId()) .build();
 * 
 * ServerWebExchange mutatedExchange = exchange.mutate()
 * .request(mutatedRequest) .build();
 * 
 * return chain.filter(mutatedExchange); }; }
 * 
 *//**
	 * Check if path is public (no token required)
	 */
/*
 * private boolean isPublicPath(String path) { return
 * path.startsWith("/admin/auth/register") ||
 * path.startsWith("/admin/auth/login") ||
 * path.startsWith("/admin/auth/setup-totp") ||
 * path.startsWith("/admin/auth/verify-totp") ||
 * path.startsWith("/admin/auth/refresh") || path.startsWith("/actuator") ||
 * path.startsWith("/swagger") || path.startsWith("/v3/api-docs"); }
 * 
 *//**
	 * Return unauthorized response
	 */
/*
 * private Mono<Void> unauthorizedResponse( ServerWebExchange exchange, String
 * message) {
 * 
 * exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
 * exchange.getResponse() .getHeaders() .add("Content-Type",
 * "application/json");
 * 
 * try { ApiResponse<String> response = new ApiResponse<>( false, message,
 * null);
 * 
 * byte[] responseBytes = objectMapper .writeValueAsString(response)
 * .getBytes(StandardCharsets.UTF_8);
 * 
 * return exchange.getResponse() .writeWith(Mono.just( exchange.getResponse()
 * .bufferFactory() .wrap(responseBytes)));
 * 
 * } catch (Exception ex) {
 * log.error("Error creating unauthorized response: {}", ex.getMessage());
 * return exchange.getResponse().setComplete(); } }
 * 
 *//**
	 * Configuration class for the filter
	 *//*
		 * public static class Config { // Add configuration properties if needed } }
		 */
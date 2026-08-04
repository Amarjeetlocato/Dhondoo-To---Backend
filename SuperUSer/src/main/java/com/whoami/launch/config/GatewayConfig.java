/*
 * package com.whoami.launch.config;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.cloud.gateway.filter.GlobalFilter; import
 * org.springframework.cloud.gateway.route.RouteLocator; import
 * org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder; import
 * org.springframework.context.annotation.Bean; import
 * org.springframework.context.annotation.Configuration; import
 * org.springframework.core.annotation.Order; import
 * org.springframework.web.server.ServerWebExchange;
 * 
 * import com.whoami.launch.filter.JwtAuthenticationFilter;
 * 
 * import reactor.core.publisher.Mono;
 * 
 *//**
	 * Gateway Configuration - Sets up routing and JWT validation
	 */
/*
 * @Configuration public class GatewayConfig {
 * 
 * @Autowired private JwtAuthenticationFilter jwtAuthenticationFilter;
 * 
 *//**
	 * Configure API Gateway routes
	 */
/*
 * @Bean public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
 * return builder.routes() // SuperUser Admin Service Routes
 * .route("admin-auth", r -> r .path("/api/admin/auth/**")
 * .uri("http://localhost:8088"))
 * 
 * .route("admin-users", r -> r .path("/api/admin/users/**") .filters(f ->
 * f.filter(jwtAuthenticationFilter.apply(new
 * JwtAuthenticationFilter.Config()))) .uri("http://localhost:8088"))
 * 
 * .route("admin-shops", r -> r .path("/api/admin/shops/**") .filters(f ->
 * f.filter(jwtAuthenticationFilter.apply(new
 * JwtAuthenticationFilter.Config()))) .uri("http://localhost:8088"))
 * 
 * .route("admin-audit", r -> r .path("/api/admin/audit/**") .filters(f ->
 * f.filter(jwtAuthenticationFilter.apply(new
 * JwtAuthenticationFilter.Config()))) .uri("http://localhost:8088"))
 * 
 * .route("dashboard", r -> r .path("/api/admin/dashboard/**") .filters(f ->
 * f.filter(jwtAuthenticationFilter.apply(new
 * JwtAuthenticationFilter.Config()))) .uri("http://localhost:8088"))
 * 
 * // Health check route (no auth required) .route("health", r -> r
 * .path("/health", "/actuator/**") .uri("http://localhost:8088"))
 * 
 * .build(); }
 * 
 *//**
	 * Global JWT filter for all requests (Order = -1 means it runs first)
	 *//*
		 * @Bean
		 * 
		 * @Order(-1) public GlobalFilter globalJwtFilter() { return (exchange, chain)
		 * -> { ServerWebExchange modifiedExchange = exchange;
		 * 
		 * // Log incoming request String requestPath =
		 * exchange.getRequest().getPath().toString();
		 * System.out.println("[GATEWAY] Incoming request: " + requestPath);
		 * 
		 * // Process through chain return chain.filter(modifiedExchange)
		 * .doOnSuccess(aVoid -> {
		 * System.out.println("[GATEWAY] Request processed successfully: " +
		 * requestPath); }) .doOnError(error -> {
		 * System.out.println("[GATEWAY] Request failed: " + requestPath + " - Error: "
		 * + error.getMessage()); }); }; } }
		 */
package com.whoami.launch.util.security;

import java.nio.charset.StandardCharsets;

import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.whoami.launch.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            WebFilterChain chain
    ) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        String method = exchange.getRequest()
                .getMethod()
                .name();

        System.out.println("JWT FILTER PATH = " + path);

        // ====================================
        // ALLOW OPTIONS (CORS PREFLIGHT)
        // ====================================
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return chain.filter(exchange);
        }

        // ====================================
        // PUBLIC ROUTES
        // ====================================
        if (
                // --------------------------------
                // ACTUATOR / KUBERNETES HEALTH
                // --------------------------------
                path.equals("/actuator/health")
                || path.equals("/actuator/health/liveness")
                || path.equals("/actuator/health/readiness")
                || path.equals("/actuator/prometheus")
                // --------------------------------
                // AUTH APIs
                // --------------------------------
                || path.equals("/api/auth/login")
                || path.equals("/api/auth/register")
                || path.equals("/api/auth/verify-otp")
                || path.equals("/api/auth/forgot-password")
                || path.equals("/api/auth/update-password")
                || path.equals("/api/auth/verify-reset-otp")
                || path.equals("/api/auth/refresh")

                // --------------------------------
                // PUBLIC HEALTH
                // --------------------------------
                || path.equals("/public/health")

                // --------------------------------
                // ADMIN AUTH
                // --------------------------------
                || path.equals("/admin/auth/register")
                || path.equals("/admin/auth/setup-totp/aksooon098098@gmail.com")
                || path.equals("/admin/auth/verify-totp")
                || path.equals("/admin/auth/login")

                // --------------------------------
                // PUBLIC SHOP APIs
                // --------------------------------
                || path.startsWith("/api/public/")
        ) {

            System.out.println(
                    "PUBLIC API SKIPPED JWT: " + path
            );

            return chain.filter(exchange);
        }

        // ====================================
// GET JWT TOKEN
// ====================================
String token = null;

// ------------------------------------
// NORMAL HTTP APIs
// Authorization: Bearer <JWT>
// ------------------------------------
String authHeader = exchange
        .getRequest()
        .getHeaders()
        .getFirst(HttpHeaders.AUTHORIZATION);

if (authHeader != null && authHeader.startsWith("Bearer ")) {
    token = authHeader.substring(7);
}

// ------------------------------------
// WEBSOCKET
// /ws?token=<JWT>
// ------------------------------------
if (token == null && "/ws".equals(path)) {

    token = exchange.getRequest()
            .getQueryParams()
            .getFirst("token");

    System.out.println(
            "WEBSOCKET TOKEN FOUND = "
                    + (token != null ? "YES" : "NO")
    );
}

// ------------------------------------
// TOKEN MISSING
// ------------------------------------
if (token == null || token.isBlank()) {

    return writeErrorResponse(
            exchange,
            HttpStatus.UNAUTHORIZED,
            "Authorization token is missing"
    );
}

        try {

            boolean valid =
                    jwtUtil.validateToken(token);

            // ====================================
            // INVALID TOKEN
            // ====================================
            if (!valid) {

                return writeErrorResponse(
                        exchange,
                        HttpStatus.UNAUTHORIZED,
                        "Invalid token"
                );
            }

        } catch (ExpiredJwtException e) {

            return writeErrorResponse(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "Your session has expired. Please login again"
            );

        } catch (Exception e) {

            e.printStackTrace();

            return writeErrorResponse(
                    exchange,
                    HttpStatus.UNAUTHORIZED,
                    "Invalid token"
            );
        }

        // ====================================
        // TOKEN VALID
        // ====================================
        return chain.filter(exchange);
    }

    // ====================================
    // COMMON JSON ERROR RESPONSE
    // ====================================
    private Mono<Void> writeErrorResponse(
            ServerWebExchange exchange,
            HttpStatus status,
            String message
    ) {

        exchange.getResponse()
                .setStatusCode(status);

        exchange.getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                    "status": %d,
                    "message": "%s"
                }
                """.formatted(
                status.value(),
                message
        );

        byte[] bytes =
                body.getBytes(StandardCharsets.UTF_8);

        return exchange.getResponse()
                .writeWith(
                        Mono.just(
                                exchange.getResponse()
                                        .bufferFactory()
                                        .wrap(bytes)
                        )
                );
    }

    @Override
    public int getOrder() {
        return 0;
    }
}


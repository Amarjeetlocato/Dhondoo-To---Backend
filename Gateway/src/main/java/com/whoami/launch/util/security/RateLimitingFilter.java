package com.whoami.launch.util.security;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class RateLimitingFilter implements WebFilter, Ordered {

    // Tier 1: Auth & OTP endpoints (strictly limited to prevent abuse & brute force)
    private static final int AUTH_LIMIT_PER_MINUTE = 10;

    // Tier 2: Search queries (database-intensive LIKE / full-text searches)
    private static final int SEARCH_LIMIT_PER_MINUTE = 30;

    // Tier 3: Orders & checkout (prevents duplicate orders / rapid charges)
    private static final int ORDER_LIMIT_PER_MINUTE = 15;

    // Tier 4: General API calls (browsing, fetching shops, products, chat, etc.)
    private static final int GENERAL_LIMIT_PER_MINUTE = 60;
    private static final int BURST_ALLOWANCE = 20; // Allows up to 80 req/min for rapid app startup / refresh

    private static class ClientWindow {
        long windowStartMs;
        final AtomicInteger authCount = new AtomicInteger(0);
        final AtomicInteger searchCount = new AtomicInteger(0);
        final AtomicInteger orderCount = new AtomicInteger(0);
        final AtomicInteger generalCount = new AtomicInteger(0);

        ClientWindow(long startMs) {
            this.windowStartMs = startMs;
        }

        void resetIfExpired(long nowMs) {
            if (nowMs - windowStartMs >= 60000L) {
                synchronized (this) {
                    if (nowMs - windowStartMs >= 60000L) {
                        windowStartMs = nowMs;
                        authCount.set(0);
                        searchCount.set(0);
                        orderCount.set(0);
                        generalCount.set(0);
                    }
                }
            }
        }
    }

    private final Map<String, ClientWindow> clientWindows = new ConcurrentHashMap<>();
    private long lastCleanupMs = System.currentTimeMillis();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String method = exchange.getRequest().getMethod() != null
                ? exchange.getRequest().getMethod().name()
                : "";

        // 1. Bypass CORS preflight requests (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getURI().getPath();

        // 2. Bypass Actuator, health probes, and internal links
        if (path.startsWith("/actuator/") || path.equals("/public/health") || path.startsWith("/internal/")) {
            return chain.filter(exchange);
        }

        // 3. Extract Client IP
        String clientIp = resolveClientIp(exchange);
        long now = System.currentTimeMillis();

        // Periodic background cleanup of stale clients every 5 minutes
        cleanupStaleClients(now);

        ClientWindow window = clientWindows.computeIfAbsent(clientIp, k -> new ClientWindow(now));
        window.resetIfExpired(now);

        // Check Rate Limit based on route tier
        int currentCount;
        int maxLimit;
        String tierName;

        if (path.startsWith("/api/auth/")) {
            tierName = "AUTH";
            maxLimit = AUTH_LIMIT_PER_MINUTE;
            currentCount = window.authCount.incrementAndGet();
        } else if (path.contains("/search/")) {
            tierName = "SEARCH";
            maxLimit = SEARCH_LIMIT_PER_MINUTE;
            currentCount = window.searchCount.incrementAndGet();
        } else if (path.equals("/api/orders/place") || path.startsWith("/api/billing/")) {
            tierName = "ORDER";
            maxLimit = ORDER_LIMIT_PER_MINUTE;
            currentCount = window.orderCount.incrementAndGet();
        } else {
            tierName = "GENERAL";
            maxLimit = GENERAL_LIMIT_PER_MINUTE + BURST_ALLOWANCE;
            currentCount = window.generalCount.incrementAndGet();
        }

        if (currentCount > maxLimit) {
            long remainingSeconds = Math.max(1, (60000L - (now - window.windowStartMs)) / 1000L);
            return writeRateLimitResponse(exchange, maxLimit, remainingSeconds, tierName);
        }

        // Add rate-limiting informational headers to response
        exchange.getResponse().getHeaders().add("X-RateLimit-Limit", String.valueOf(maxLimit));
        exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", String.valueOf(Math.max(0, maxLimit - currentCount)));

        return chain.filter(exchange);
    }

    private String resolveClientIp(ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }
        if (exchange.getRequest().getRemoteAddress() != null) {
            return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }
        return "127.0.0.1";
    }

    private void cleanupStaleClients(long nowMs) {
        if (nowMs - lastCleanupMs > 300000L) { // 5 minutes
            lastCleanupMs = nowMs;
            clientWindows.entrySet().removeIf(entry -> nowMs - entry.getValue().windowStartMs > 180000L);
        }
    }

    private Mono<Void> writeRateLimitResponse(
            ServerWebExchange exchange,
            int limit,
            long retryAfterSeconds,
            String tierName
    ) {
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().set(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds));
        exchange.getResponse().getHeaders().set("X-RateLimit-Limit", String.valueOf(limit));
        exchange.getResponse().getHeaders().set("X-RateLimit-Remaining", "0");

        String body = """
                {
                    "status": 429,
                    "error": "TOO_MANY_REQUESTS",
                    "tier": "%s",
                    "message": "Too many requests. Please slow down and wait %d seconds.",
                    "retryAfter": %d
                }
                """.formatted(tierName, retryAfterSeconds, retryAfterSeconds);

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
        );
    }

    @Override
    public int getOrder() {
        return -1; // Execute before JwtAuthenticationFilter (order 0)
    }
}

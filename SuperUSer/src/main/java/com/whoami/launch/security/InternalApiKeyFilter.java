package com.whoami.launch.security;

import java.io.IOException;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class InternalApiKeyFilter
        extends OncePerRequestFilter {

    @Value("${internal.api.key}")
    private String configuredKey;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (!uri.startsWith("/internal")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey =
                request.getHeader("X-API-KEY");

        if (apiKey == null ||
                !apiKey.equals(configuredKey)) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter()
                    .write("Invalid API Key");

            return;
        }

        filterChain.doFilter(
                request,
                response);
    }
}
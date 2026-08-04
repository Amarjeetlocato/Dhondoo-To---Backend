package com.whoami.launch.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Skip OPTIONS requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestURI = request.getRequestURI();

        // Public APIs
        if (requestURI.contains("/auth/login")
                || requestURI.contains("/auth/register")
                || requestURI.contains("/auth/verify-otp")
                || requestURI.contains("/auth/forgot-password")
                || requestURI.contains("/auth/update-password")
                || requestURI.contains("/auth/verify-reset-otp")
                || requestURI.contains("/auth/verify-reset-otp")
                || requestURI.contains("/auth/refresh")) {

            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("🌐 Request URI: " + requestURI);

        Collections.list(request.getHeaderNames())
                .forEach(header ->
                        System.out.println(
                                "Header: "
                                        + header
                                        + " = "
                                        + request.getHeader(header)
                        )
                );

        String authHeader =
                request.getHeader("Authorization");

        String token = null;
        String email = null;
        String userId = null;

        if (authHeader != null
                && authHeader.startsWith("Bearer ")) {

            token = authHeader.substring(7);

            try {

                email = jwtHelper.getEmailFromToken(token);
                userId = jwtHelper.getUserIdFromToken(token);

                System.out.println("📧 Email : " + email);
                System.out.println("🆔 UserId : " + userId);

            } catch (Exception e) {

                System.out.println(
                        "❌ JWT Parsing Error : "
                                + e.getMessage()
                );
            }
        }

        if (email != null
                && SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {

            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(email);

            if (jwtHelper.validateToken(
                    token,
                    userDetails.getUsername()
            )) {

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                auth.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(auth);

                /*
                 * Make userId available
                 * everywhere in request scope
                 */
                request.setAttribute(
                        "userId",
                        userId
                );

                request.setAttribute(
                        "email",
                        email
                );

                System.out.println(
                        "✅ Authenticated User : "
                                + email
                );

                System.out.println(
                        "✅ UserId : "
                                + userId
                );
            }
        }

        filterChain.doFilter(request, response);
    }
}
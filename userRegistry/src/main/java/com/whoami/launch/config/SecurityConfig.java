package com.whoami.launch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.whoami.launch.security.JwtAuthenticationEntryPoint;
import com.whoami.launch.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint point;
    private final JwtAuthenticationFilter filter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(
            JwtAuthenticationEntryPoint point,
            JwtAuthenticationFilter filter,
            AuthenticationProvider authenticationProvider
    ) {
        this.point = point;
        this.filter = filter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

            // Disable CSRF
            .csrf(csrf -> csrf.disable())

            // Stateless JWT
            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            // JWT Exception Handler
            .exceptionHandling(ex ->
                    ex.authenticationEntryPoint(point)
            )

            // Authorization
            .authorizeHttpRequests(auth -> auth

                    // Public APIs
                    .requestMatchers(
                            "/api/auth/register",
                            "/api/auth/login",
                            "/api/auth/verify-otp",
                            "/api/auth/resend-otp",
                            "/api/auth/forgot-password",
                            "/api/auth/verify-reset-otp",
                            "/api/auth/update-password",
                            "/api/auth/refresh"
                    ).permitAll()

                    // Preflight requests
                    .requestMatchers(
                            HttpMethod.OPTIONS,
                            "/**"
                    ).permitAll()

                    // Protected APIs
                    .anyRequest().authenticated()
            )

            // Authentication Provider
            .authenticationProvider(authenticationProvider)

            // JWT Filter
            .addFilterBefore(
                    filter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
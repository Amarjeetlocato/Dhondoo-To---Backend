package com.whoami.launch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
            AuthenticationProvider authenticationProvider) {

        this.point = point;
        this.filter = filter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS))

            .exceptionHandling(ex ->
                    ex.authenticationEntryPoint(point))

            .authorizeHttpRequests(auth -> auth

                    .requestMatchers(
                            "/admin/auth/register",
                            "/admin/auth/login",
                            "/admin/auth/setup-totp/**",
                            "/admin/auth/verify-totp",
                            "/admin/auth/refresh"
                    ).permitAll()

                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")

                    .anyRequest()
                    .authenticated()
            )

            .authenticationProvider(authenticationProvider)

            .addFilterBefore(
                    filter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
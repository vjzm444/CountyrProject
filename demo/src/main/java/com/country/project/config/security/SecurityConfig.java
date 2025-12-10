package com.country.project.config.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.country.project.config.jwt.JwtAuthenticationEntryPoint;
import com.country.project.config.jwt.JwtAuthenticationFilter;
import com.country.project.config.jwt.JwtTokenProvider;

/**
 * 시큘리티 설정
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(JwtTokenProvider jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    /**
     * 필터 처리
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/webjars/**",
                "/v3/api-docs.yaml"
            ).permitAll()

            // H2 콘솔 허용 (모든 경로)
            .requestMatchers("/h2-console/**").permitAll()
            .anyRequest().authenticated() // 나머지는 전부 JWT 인증
        )

        // H2 콘솔은 frameOptions SAMEORIGIN 필요
        .headers(headers -> headers
            .frameOptions(frame -> frame.sameOrigin())
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        )
        // jwt 처리
        .addFilterBefore(
            new JwtAuthenticationFilter(jwtAuthenticationFilter),
            UsernamePasswordAuthenticationFilter.class
        );


        return http.build();
    }
}
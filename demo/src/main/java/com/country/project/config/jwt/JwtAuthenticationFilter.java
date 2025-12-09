package com.country.project.config.jwt;


import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 검증
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    //실질적 jwt토큰검증
    private final JwtTokenProvider jwtTokenProvider;

    //생성자
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //검증처리
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        try {
            if (token != null) {

                // Claims 반환
                Claims claims = jwtTokenProvider.validateToken(token);

                // claims 가 null 이 아니면 인증 처리
                if (claims != null) {
                    Authentication authentication = createAuthentication(claims);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            // 예외를 던지지 말아야 함 → Security가 AuthenticationEntryPoint 로 처리하게 됨
        }

        filterChain.doFilter(request, response);
    }

    // Authorization Bearer 파싱
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Claims → Authentication 변환
    private Authentication createAuthentication(Claims claims) {
        String username = claims.getSubject();

        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.emptyList()  // 권한 필요 없으면 빈 리스트
        );
    }
}

package com.country.project.config.jwt;


import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


/**
 * JWT토큰검증
 */
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    public JwtTokenProvider() {

        // TODO: 별도로 안전한 장소에 시크릿키 저장 (환경변수 or 외부 설정 파일)
        // 현재는 테스트용 임시 키
        String secret = "a-string-secret-at-least-256-bits-long"; 

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Authentication 생성
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String subject = claims.get("sub", String.class);
        if (subject == null) subject = claims.getSubject();
        return new UsernamePasswordAuthenticationToken(subject, "", null);
    }

    // JWT 유효성 검사
    public Claims validateToken(String token) {
        try {
            //TODO 추후 만료시간 검증도 고려해야함.
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    // Claims 파싱
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

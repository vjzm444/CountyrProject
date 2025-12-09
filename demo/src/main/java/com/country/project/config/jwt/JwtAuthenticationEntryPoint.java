package com.country.project.config.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.country.project.config.exception.ErrorCode;
import com.country.project.config.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 인증 실패(401 Unauthorized)가 발생했을 때
 * Spring Security가 자동으로 호출하는 엔트리 포인트 클래스.
 *
 * - 잘못된 토큰
 * - 토큰 없음
 * - 인증 헤더 없음
 * 
 * 위 상황에서 JSON 형태의 에러 응답을 클라이언트에게 내려준다.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    //로거
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        logger.error("Unauthorized error: {}", authException.getMessage());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // ErrorResponse를 JSON으로 내려줌
        // 커스텀 에러 응답 반환
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.UNAUTHORIZED);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}

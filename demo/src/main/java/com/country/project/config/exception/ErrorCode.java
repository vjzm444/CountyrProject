package com.country.project.config.exception;


import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 커스텀 에러코드 모음집
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    /*
     * 400 BAD_REQUEST: 잘못된 요청
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    /*
     * 404 NOT_FOUND: 리소스를 찾을 수 없음
     */
    POSTS_NOT_FOUND(HttpStatus.NOT_FOUND, "정보를 찾을 수 없습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원정보를 찾을 수 없습니다."),

    USERPW_NOT_FOUND(HttpStatus.NOT_FOUND, "비밀번호 오류 입니다."),

    /*
     * 405 METHOD_NOT_ALLOWED: 허용되지 않은 Request Method 호출
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메서드입니다."),

    /*
     * 500 INTERNAL_SERVER_ERROR: 내부 서버 오류
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류입니다."),
    /*
     * 415 UNSUPPORTED_MEDIA_TYPE: 파라미터 오류
     */
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "파라메터 오류입니다."),
    /*
     * 401 UNAUTHORIZED: 인증 오류
        -JwtAuthenticationEntryPoint 호출됨
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authentication is required or the token is invalid")

    ;

    private final HttpStatus status;
    private final String message;

}
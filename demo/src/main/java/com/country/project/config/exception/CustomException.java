package com.country.project.config.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 커스터마이징 한 에러코드를 설정
 */
@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

}
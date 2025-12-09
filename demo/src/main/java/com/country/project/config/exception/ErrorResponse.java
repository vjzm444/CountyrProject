package com.country.project.config.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
/**
 * HTTP에러관련 모델
 */
@Getter
public class ErrorResponse {

     private final String timestamp = 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


    private final int status;
    private final String error;
    private final String code;
    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.error = errorCode.getStatus().name();
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
    }

}
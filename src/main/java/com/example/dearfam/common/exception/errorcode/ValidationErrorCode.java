package com.example.dearfam.common.exception.errorcode;

import com.example.dearfam.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ValidationErrorCode implements ErrorCode {
    INVALID_REQUEST("유효하지 않은 Request 입니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public HttpStatus defaultHttpStatus() {
        return httpStatus;
    }

    @Override
    public String defaultMessage() {
        return message;
    }

    @Override
    public ValidationException defaultException() {
        return new ValidationException(this);
    }

    @Override
    public ValidationException defaultException(Throwable cause) {
        return new ValidationException(this, cause);
    }
}

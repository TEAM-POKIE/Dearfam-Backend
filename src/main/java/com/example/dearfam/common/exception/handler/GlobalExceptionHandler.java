package com.example.dearfam.common.exception.handler;

import com.example.dearfam.common.dto.exception.ApiResponseError;
import com.example.dearfam.common.exception.CustomException;
import com.example.dearfam.common.exception.NoContentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponseError> handleException(CustomException exception) {
        ApiResponseError responseError = ApiResponseError.of(exception);
        HttpStatus httpStatus = exception
                .getErrorCode()
                .defaultHttpStatus();

        return new ResponseEntity<>(responseError, httpStatus);
    }

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<?> handleNoContentException(NoContentException exception) {
        return ResponseEntity.noContent().build();
    }

}

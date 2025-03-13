package com.example.dearfam.common.dto.exception;

import com.example.dearfam.common.exception.CustomException;
import com.example.dearfam.common.exception.errorcode.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

/**
 *
 * @param code 에러 코드 명
 * @param status 상태 코드 값
 * @param name 오류 이름
 * @param message 오류 메시지
 * @param cause
 * @param timestamp 발생 시각
 */

@Builder
public record ApiResponseError(
        String code,
        Integer status,
        String name,
        String message,
        @JsonInclude(Include.NON_EMPTY)List<ApiSimpleError> cause,
        Instant timestamp
) {
    public static ApiResponseError of(CustomException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        String errorName = exception.getClass().getName();
        errorName = errorName.substring(errorName.lastIndexOf(".") + 1);

        return ApiResponseError.builder()
                .code(errorCode.name())
                .status(errorCode.defaultHttpStatus().value())
                .name(errorName)
                .message(exception.getMessage())
                .cause(ApiSimpleError.listOfCauseSimpleError(exception.getCause()))
                .build();
    }

    public ApiResponseError {
        if (code == null) code = "API ERROR";
        if (status == null) status = 500;
        if (name == null) name = "ApiError";
        if (message == null||message.isBlank()) message = "Api 오류";
        if (timestamp == null) timestamp = Instant.now();
    }
}

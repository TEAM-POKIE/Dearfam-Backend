package com.example.dearfam.domain.family.exception;

import com.example.dearfam.common.exception.errorcode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum FamilyErrorCode implements ErrorCode {
    FAMILY_NOT_REGISTERED("가족이 등록되어 있지 않습니다.", HttpStatus.NOT_FOUND),
    FAMILY_NOT_FOUND("가족을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FAMILY_ALREADY_EXISTS("이미 가족이 존재합니다.", HttpStatus.CONFLICT),
    DEFAULT("가족 관련 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus status;

    @Override
    public HttpStatus defaultHttpStatus() {
        return status;
    }

    @Override
    public String defaultMessage() {
        return message;
    }

    @Override
    public FamilyException defaultException() {
        return new FamilyException(this);
    }

    @Override
    public FamilyException defaultException(Throwable cause) {
        return new FamilyException(this, cause);
    }
}

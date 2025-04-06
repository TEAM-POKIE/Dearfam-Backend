package com.example.dearfam.domain.family.exception;

import com.example.dearfam.common.exception.CustomException;
import com.example.dearfam.common.exception.errorcode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum InviteErrorCode implements ErrorCode {
    INVALID_INVITE_CODE("유효하지 않은 초대 코드입니다.", HttpStatus.BAD_REQUEST),
    INVITE_CODE_EXPIRED("초대 코드가 만료되었습니다.", HttpStatus.GONE);


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
    public CustomException defaultException() {
        return new InviteException(this);
    }

    @Override
    public CustomException defaultException(Throwable cause) {
        return new InviteException(this, cause);
    }
}

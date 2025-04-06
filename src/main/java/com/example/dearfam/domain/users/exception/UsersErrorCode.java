package com.example.dearfam.domain.users.exception;

import com.example.dearfam.common.exception.errorcode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum UsersErrorCode implements ErrorCode {
    NICKNAME_ALREADY_EXISTS("이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_USER_REQUEST("유효하지 않은 사용자 요청입니다.", HttpStatus.BAD_REQUEST),
    INVALID_ROLE("허용되지 않은 역할입니다.", HttpStatus.BAD_REQUEST),
    PROFILE_IMAGE_PROCESSING_ERROR("프로필 이미지 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_DELETE_FAILED("사용자 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DEFAULT("사용자 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus status;

    @Override
    public String defaultMessage() {
        return message;
    }

    @Override
    public HttpStatus defaultHttpStatus() {
        return status;
    }

    @Override
    public UsersException defaultException() {
        return new UsersException(this);
    }

    @Override
    public UsersException defaultException(Throwable cause) {
        return new UsersException(this, cause);
    }
}
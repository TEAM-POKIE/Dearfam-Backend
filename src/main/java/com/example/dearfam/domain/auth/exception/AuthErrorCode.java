package com.example.dearfam.domain.auth.exception;

import com.example.dearfam.common.exception.errorcode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    NOT_SUPPORTED_SOCIAL_PROVIDER("지원하지 않는 소셜 제공자입니다.", HttpStatus.BAD_REQUEST),
    GET_USER_INFO_FAILED_FROM_SOCIAL_PROVIDER("소셜 제공자로부터 유저 정보를 추출하지 못했습니다.", HttpStatus.FORBIDDEN),
    CANNOT_FIND_KAKAO_USER_ID("카카오 유저 ID를 찾지 못했습니다,", HttpStatus.FORBIDDEN),
    KAKAO_EMAIL_IS_NULL("카카오 이메일 정보를 가져오지 못했습니다.", HttpStatus.BAD_REQUEST),
    KAKAO_AUTH_CODE_INVALID( "인가 코드 또는 Redirect URI가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    KAKAO_PROFILE_IS_NULL("카카오 유저의 프로필 사진을 가져오지 못했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    KAKAO_ACCESS_TOKEN_IS_NULL("카카오로부터 엑세스 토큰을 받지 못했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    KAKAO_COMMUNICATION_ERROR("카카오 서버와 통신 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DEFAULT("보안 관련 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

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
    public AuthException defaultException() {
        return new AuthException(this);
    }

    @Override
    public AuthException defaultException(Throwable cause) {
        return new AuthException(this, cause);
    }
}

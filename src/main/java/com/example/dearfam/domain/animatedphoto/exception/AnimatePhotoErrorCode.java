package com.example.dearfam.domain.animatedphoto.exception;

import com.example.dearfam.common.exception.errorcode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AnimatePhotoErrorCode implements ErrorCode {
    RESPONSE_NULL("AI 서버의 응답이 null입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    AI_RESPONSE_FAIL("AI 서버의 응답이 실패 상태입니다.", HttpStatus.BAD_GATEWAY),
    UNAUTHORIZED_VIDEO_DELETE("같은 가족만 삭제가 가능합니다.", HttpStatus.UNAUTHORIZED),
    VIDEO_NOT_FOUND("영상을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    IMAGE_PROCESSING_ERROR("이미지 파일 처리 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST),
    AI_SERVER_COMMUNICATION_ERROR("AI 서버와의 통신 중 오류가 발생했습니다.", HttpStatus.GATEWAY_TIMEOUT);

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
    public AnimatePhotoException defaultException() {
        return new AnimatePhotoException(this);
    }

    @Override
    public AnimatePhotoException defaultException(Throwable cause) {
        return new AnimatePhotoException(this, cause);
    }
}

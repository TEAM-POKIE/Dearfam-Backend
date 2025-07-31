package com.example.dearfam.domain.diary.exception;

import com.example.dearfam.common.exception.errorcode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum DiaryErrorCode implements ErrorCode {
    POST_ID_REQUIRED("게시글 ID가 제공되지 않았습니다.", HttpStatus.BAD_REQUEST),
    MEMORY_POST_NOT_FOUND("해당 ID의 게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    AI_SERVER_FAILED("AI 서버로부터 그림일기 콘텐츠를 생성하지 못했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    AI_COMMUNICATION_ERROR("AI 서버와 통신 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

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
    public DiaryException defaultException() {
        return new DiaryException(this);
    }

    @Override
    public DiaryException defaultException(Throwable cause) {
        return new DiaryException(this, cause);
    }
}

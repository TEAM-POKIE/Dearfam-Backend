package com.example.dearfam.domain.memoryposts.memorypost.exception;

import com.example.dearfam.common.exception.errorcode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum MemoryPostErrorCode implements ErrorCode {
    MEMORY_POST_NOT_FOUND("추억 게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_MEMORY_POST_ACCESS("게시글 작성자만 수정 또는 삭제가 가능합니다.", HttpStatus.FORBIDDEN),
    DEFAULT("추억 게시글 처리 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

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
    public MemoryPostException defaultException() {
        return new MemoryPostException(this);
    }

    @Override
    public MemoryPostException defaultException(Throwable cause) {
        return new MemoryPostException(this, cause);
    }
}

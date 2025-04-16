package com.example.dearfam.domain.memoryposts.comment.exception;

import com.example.dearfam.common.exception.errorcode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum MemoryPostCommentErrorCode implements ErrorCode {
    COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_WRITER("삭제 권한이 없는 사용자입니다.", HttpStatus.FORBIDDEN),
    DEFAULT("게시글 댓글 처리 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

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
    public MemoryPostCommentException defaultException() {
        return new MemoryPostCommentException(this);
    }

    @Override
    public MemoryPostCommentException defaultException(Throwable cause) {
        return new MemoryPostCommentException(this, cause);
    }
}

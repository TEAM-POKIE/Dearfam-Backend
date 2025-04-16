package com.example.dearfam.domain.memoryposts.comment.exception;

import com.example.dearfam.common.exception.CustomException;
import com.example.dearfam.common.exception.errorcode.ErrorCode;

public class MemoryPostCommentException extends CustomException {
    public MemoryPostCommentException() {
      super();
    }

    public MemoryPostCommentException(String message) {
      super(message);
    }

    public MemoryPostCommentException(String message, Throwable cause) {
      super(message, cause);
    }

    public MemoryPostCommentException(ErrorCode errorCode) {
      super(errorCode);
    }

    public MemoryPostCommentException(ErrorCode errorCode, Throwable cause) {
      super(errorCode, cause);
    }
}

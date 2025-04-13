package com.example.dearfam.domain.memoryposts.memorypost.exception;

import com.example.dearfam.common.exception.CustomException;
import com.example.dearfam.common.exception.errorcode.ErrorCode;

public class MemoryPostException extends CustomException {
    public MemoryPostException() {
        super();
    }

    public MemoryPostException(String message) {
        super(message);
    }

    public MemoryPostException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemoryPostException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MemoryPostException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}

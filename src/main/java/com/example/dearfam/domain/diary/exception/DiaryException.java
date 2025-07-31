package com.example.dearfam.domain.diary.exception;

import com.example.dearfam.common.exception.CustomException;
import com.example.dearfam.common.exception.errorcode.ErrorCode;

public class DiaryException extends CustomException {

    public DiaryException() {
        super();
    }

    public DiaryException(String message) {
        super(message);
    }

    public DiaryException(String message, Throwable cause) {
        super(message, cause);
    }

    public DiaryException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DiaryException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

}

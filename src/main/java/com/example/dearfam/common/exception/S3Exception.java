package com.example.dearfam.common.exception;

import com.example.dearfam.common.exception.errorcode.ErrorCode;

public class S3Exception extends CustomException {
    public S3Exception() {
        super();
    }

    public S3Exception(String message) {
        super(message);
    }

    public S3Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public S3Exception(ErrorCode errorCode) {
        super(errorCode);
    }

    public S3Exception(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}

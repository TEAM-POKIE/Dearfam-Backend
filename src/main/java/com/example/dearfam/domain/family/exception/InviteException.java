package com.example.dearfam.domain.family.exception;

import com.example.dearfam.common.exception.CustomException;
import com.example.dearfam.common.exception.errorcode.ErrorCode;

public class InviteException extends CustomException {

    public InviteException() {
        super();
    }

    public InviteException(String message) {
        super(message);
    }

    public InviteException(String message, Throwable cause) {
        super(message, cause);
    }

    public InviteException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InviteException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

}

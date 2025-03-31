package com.example.dearfam.domain.family.exception;

import com.example.dearfam.common.exception.CustomException;
import com.example.dearfam.common.exception.errorcode.ErrorCode;

public class FamilyException extends CustomException {

    public FamilyException() {
        super();
    }

    public FamilyException(String message) {
        super(message);
    }

    public FamilyException(String message, Throwable cause) {
        super(message, cause);
    }

    public FamilyException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FamilyException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

}

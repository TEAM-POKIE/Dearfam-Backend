package com.example.dearfam.domain.users.exception;

import com.example.dearfam.common.exception.CustomException;
import com.example.dearfam.common.exception.errorcode.ErrorCode;

public class UsersException extends CustomException {

    public UsersException() {
        super();
    }

    public UsersException(String message) {
        super(message);
    }

    public UsersException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsersException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UsersException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }


}
package com.example.dearfam.domain.animatedphoto.exception;

import com.example.dearfam.common.exception.CustomException;
import com.example.dearfam.common.exception.errorcode.ErrorCode;

public class AnimatePhotoException extends CustomException {

    public AnimatePhotoException() {
        super();
    }

    public AnimatePhotoException(String message) {
        super(message);
    }

    public AnimatePhotoException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnimatePhotoException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AnimatePhotoException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

}
package com.example.dearfam.common.exception;

import com.example.dearfam.common.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

public class NoContentException extends CustomException {

    public NoContentException() {
        super(new NoContentErrorCode());
    }

    public NoContentException(String message) {
        super(new NoContentErrorCode(message));
    }

    public NoContentException(String message, Throwable cause) {
        super(new NoContentErrorCode(message), cause);
    }

    private static class NoContentErrorCode implements ErrorCode {
        private final String message;

        public NoContentErrorCode() {
            this.message = "요청한 데이터가 없습니다.";
        }

        public NoContentErrorCode(String message) {
            this.message = message;
        }

        @Override
        public String name() {
            return "NO_CONTENT";
        }

        @Override
        public HttpStatus defaultHttpStatus() {
            return HttpStatus.NO_CONTENT;
        }

        @Override
        public String defaultMessage() {
            return message;
        }

        @Override
        public RuntimeException defaultException() {
            return new NoContentException();
        }

        @Override
        public RuntimeException defaultException(Throwable cause) {
            return new NoContentException(message, cause);
        }
    }
}
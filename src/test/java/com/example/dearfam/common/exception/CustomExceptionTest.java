package com.example.dearfam.common.exception;

import com.example.dearfam.common.exception.errorcode.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomExceptionTest {

    @Test
    void shouldCreateCustomExceptionWithDefaultErrorCode() {
        CustomException customException = new CustomException();

        assertThat(customException.getErrorCode().name()).isEqualTo("SERVER_ERROR");
        assertThat(customException.getErrorCode().defaultMessage()).isEqualTo("서버 오류");
    }

    @Test
    void shouldCreateCustomExceptionWithErrorCode() {
        CustomException customException = new CustomException("메시지 테스트");

        assertThat(customException.getMessage()).isEqualTo("메시지 테스트");
    }

    @Test
    void shouldCreateCustomExceptionWithTestErrorCode() {
        ErrorCode errorCode = new TestErrorCode();
        CustomException customException = new CustomException(errorCode);

        assertThat(customException.getMessage()).isEqualTo("테스트 에러 발생");
        assertThat(customException.getErrorCode().defaultMessage()).isEqualTo("테스트 에러 발생");
        assertThat(customException.getErrorCode().defaultHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private static class TestErrorCode implements ErrorCode {
        @Override
        public String name() {
            return "TEST_ERROR";
        }

        @Override
        public HttpStatus defaultHttpStatus() {
            return HttpStatus.BAD_REQUEST;
        }

        @Override
        public String defaultMessage() {
            return "테스트 에러 발생";
        }

        @Override
        public RuntimeException defaultException() {
            return new CustomException(this);
        }

        @Override
        public RuntimeException defaultException(Throwable cause) {
            return new CustomException(this, cause);
        }
    }
}
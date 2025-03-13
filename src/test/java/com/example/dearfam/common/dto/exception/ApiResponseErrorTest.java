package com.example.dearfam.common.dto.exception;

import com.example.dearfam.common.exception.CustomException;
import com.example.dearfam.common.exception.errorcode.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ApiResponseErrorTest {

    @Test
    void of_ShouldCreateApiResponseError_FromCustomException() {
        // CustomException 에러 발생 시, ApiResponseError 객체가 잘 생성되는지 확인하는 테스트 코드
        CustomException exception = new CustomException(new TestErrorCode());
        ApiResponseError responseError = ApiResponseError.of(exception);

        assertThat(responseError).isNotNull();
        assertThat(responseError.code()).isEqualTo("TEST_ERROR");
        assertThat(responseError.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(responseError.message()).isEqualTo("테스트 에러 발생");
    }

    private static class TestErrorCode implements ErrorCode {
        @Override
        public String name() {
            return "TEST_ERROR";
        }

        @Override
        public HttpStatus defaultHttpStatus() {
            return HttpStatus.INTERNAL_SERVER_ERROR;
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
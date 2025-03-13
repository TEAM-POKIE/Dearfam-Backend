package com.example.dearfam.common.exception.handler;

import com.example.dearfam.common.dto.exception.ApiResponseError;
import com.example.dearfam.common.exception.CustomException;
import com.example.dearfam.common.exception.NoContentException;
import com.example.dearfam.common.exception.errorcode.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleException_에러를반환해야함() {
        // Exception 발생 시 에러 처리 되는지 확인하는 테스트 코드
        TestErrorCode testErrorCode = new TestErrorCode();
        CustomException exception = new CustomException(testErrorCode);

        ResponseEntity<ApiResponseError> response = handler.handleException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("TEST_ERROR");
        assertThat(response.getBody().message()).isEqualTo("테스트 에러 발생");
    }

    @Test
    void handleNoContentException_204에러반환해야함() {
        // 응답 본문이 필요없는 경우에 204 에러 객체를 반환하는지 확인하는 테스트 코드
        NoContentException exception = new NoContentException();
        ResponseEntity<ApiResponseError> response = handler.handleException(exception);
        assertThat(response.getBody()).isNotNull();
        System.out.println(response.getBody());
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
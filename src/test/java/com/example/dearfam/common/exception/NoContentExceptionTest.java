package com.example.dearfam.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class NoContentExceptionTest {

    @Test
    void shouldReturnDefaultNoContentError() {
        NoContentException noContentException = new NoContentException();

        assertThat(noContentException.getErrorCode().defaultHttpStatus()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(noContentException.getErrorCode().name()).isEqualTo("NO_CONTENT");
        assertThat(noContentException.getErrorCode().defaultMessage()).isEqualTo("요청한 데이터가 없습니다.");

    }

    @Test
    void shouldReturnCustomMessageNoContentError() {
        String message = "NoContentException 메시지 테스트";
        NoContentException noContentException = new NoContentException(message);

        assertThat(noContentException.getErrorCode().defaultMessage()).isEqualTo(message);
    }

}
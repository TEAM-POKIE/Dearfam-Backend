package com.example.dearfam.common.dto.exception;

import com.example.dearfam.common.exception.CustomException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiSimpleErrorTest {

    @Test
    void shouldConvertExceptionToSimpleError_메시지없는기본버전() {
        Throwable cause = new CustomException();

        List<ApiSimpleError> simpleErrorList = ApiSimpleError.listOfCauseSimpleError(cause);

        assertThat(simpleErrorList).hasSize(1);
        assertThat(simpleErrorList.get(0).field()).isEqualTo("CustomException");
        assertThat(simpleErrorList.get(0).message()).isEqualTo("No message provided");
    }

    @Test
    void shouldConvertExceptionToSimpleError_메시지있는버전() {
        Throwable cause = new CustomException("ApiSimpleError 클래스 테스트 중");

        List<ApiSimpleError> simpleErrorList = ApiSimpleError.listOfCauseSimpleError(cause);

        assertThat(simpleErrorList).hasSize(1);
        assertThat(simpleErrorList.get(0).field()).isEqualTo("CustomException");
        assertThat(simpleErrorList.get(0).message()).isEqualTo("ApiSimpleError 클래스 테스트 중");
    }

    @Test
    void shouldConvertExceptionToSimpleError_에러깊이2단계() {
        // depth가 2인 경우에도 예외가 잘 처리되는지 확인하는 테스트 코드
        Throwable cause = new CustomException("depth 2 예외");
        Throwable cause2 = new RuntimeException("depth 1 예외", cause);
        List<ApiSimpleError> simpleErrorList = ApiSimpleError.listOfCauseSimpleError(cause2);

        assertThat(simpleErrorList).hasSize(2);
        assertThat(simpleErrorList.get(0).field()).isEqualTo("RuntimeException");
        assertThat(simpleErrorList.get(0).message()).isEqualTo("depth 1 예외");

        System.out.println(simpleErrorList);

        assertThat(simpleErrorList.get(1).field()).isEqualTo("CustomException");
        assertThat(simpleErrorList.get(1).message()).isEqualTo("depth 2 예외");
    }
}
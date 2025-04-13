package com.example.dearfam.common.dto.exception;

import lombok.Builder;
import lombok.NonNull;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Objects;

@Builder
public record ApiSimpleError(@NonNull String field, @NonNull String message) {
    public static List<ApiSimpleError> listOfCauseSimpleError(Throwable cause) {
        return List.of(arrayOfCauseSimpleError(cause));
    }

    public static ApiSimpleError[] arrayOfCauseSimpleError(Throwable cause) {
        int depth = 0;
        ApiSimpleError[] subErrors;
        Throwable currentCause = cause; // 현재 예외를 저장

        while (currentCause != null) { // 예외 체인의 깊이 계산
            currentCause = currentCause.getCause();
            depth++;
        }

        subErrors = new ApiSimpleError[depth]; // 예외 깊이만큼 배열 생성
        currentCause = cause; // 다시 초기화 (예외를 따라가며 저장하기 위해)

        for (int i = 0; i < depth; i++) { // 예외 깊이만큼 반복
            String errorFullName = currentCause.getClass().getSimpleName(); // 예외 타입(클래스) 이름 가져옴
            String field = errorFullName.substring(errorFullName.lastIndexOf('.') + 1); //클래스가 패키지 포함된 이름일 경우를 대비한 코드
            subErrors[i] = ApiSimpleError.builder()
                    .field(field)
                    .message(currentCause.getLocalizedMessage() != null ? currentCause.getLocalizedMessage() : "No message provided")
                    .build();

            // currentCause를 갱신해야, 예외체인에 대한 탐색이 올바르게 진행됨
            currentCause = currentCause.getCause();
        }

        return subErrors;
    }

    // Validation Error 기반 (FieldError 목록 처리)
    public static List<ApiSimpleError> ofFieldErrors(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .map(error -> new ApiSimpleError(error.getField(), Objects.requireNonNull(error.getDefaultMessage())))
                .toList();
    }

}

package com.example.dearfam.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response<T> {
    private int code; // 응답 코드
    private String message; // 응답 메시지
    private T data; // 제네릭 타입의 응답 데이터

    public static <T> Response<T> of(int code, String message, T data) {
        return new Response<>(code, message, data);
    }

    public static <T> Response<T> data(T data) {
        return new Response<>(0, "", data);
    }

    public static <T> Response<T> data(String message, T data) {
        return new Response<>(0, message, data);
    }
}
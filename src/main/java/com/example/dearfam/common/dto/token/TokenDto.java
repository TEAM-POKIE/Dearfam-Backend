package com.example.dearfam.common.dto.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDto {
    // jwt 내부에 담긴 사용자 정보를 담는 dto
    private final Long userId;
    private String userRole;
}

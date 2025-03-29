package com.example.dearfam.domain.users.controller.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserNicknameRequest {
    @NotEmpty
    private String nickname;
}

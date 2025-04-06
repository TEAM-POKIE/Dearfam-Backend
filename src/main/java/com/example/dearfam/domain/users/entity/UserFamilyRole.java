package com.example.dearfam.domain.users.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "가족 내 역할", example = "FATHER", allowableValues = {"MOTHER", "FATHER", "SON", "DAUGHTER"})
public enum UserFamilyRole {
    FATHER,
    MOTHER,
    SON,
    DAUGHTER;

    public boolean isParent() {
        return this == MOTHER || this == FATHER;
    }

    public boolean isChild() {
        return this == SON || this == DAUGHTER;
    }
}
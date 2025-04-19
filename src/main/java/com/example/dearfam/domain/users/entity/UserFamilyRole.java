package com.example.dearfam.domain.users.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "가족 내 역할", example = "FATHER", allowableValues = {"MOTHER", "FATHER", "SON", "DAUGHTER"})
public enum UserFamilyRole {
    FATHER(0),
    MOTHER(1),
    SON(2),
    DAUGHTER(2);

    private final int sortOrder;

    UserFamilyRole(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isParent() {
        return this == MOTHER || this == FATHER;
    }

    public boolean isChild() {
        return this == SON || this == DAUGHTER;
    }
}
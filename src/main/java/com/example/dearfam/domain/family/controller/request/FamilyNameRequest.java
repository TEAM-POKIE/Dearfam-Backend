package com.example.dearfam.domain.family.controller.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FamilyNameRequest {
    @NotEmpty
    private final String familyName;
}

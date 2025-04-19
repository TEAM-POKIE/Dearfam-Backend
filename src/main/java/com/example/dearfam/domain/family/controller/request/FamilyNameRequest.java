package com.example.dearfam.domain.family.controller.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FamilyNameRequest {
    @NotEmpty
    private String familyName;
}

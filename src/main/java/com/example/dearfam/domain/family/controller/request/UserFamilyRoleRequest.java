package com.example.dearfam.domain.family.controller.request;

import com.example.dearfam.domain.users.entity.UserFamilyRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserFamilyRoleRequest {

    @NotNull(message = "가족 역할은 필수입니다")
    private UserFamilyRole familyRole;

}

package com.example.dearfam.domain.family.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class InviteLinkResponse {
    private String link;
    private String code;
    private LocalDateTime expiresAt;

    public static InviteLinkResponse from(String link, String code, LocalDateTime expiresAt) {
        return InviteLinkResponse.builder()
                .link(link)
                .code(code)
                .expiresAt(expiresAt)
                .build();
    }
}

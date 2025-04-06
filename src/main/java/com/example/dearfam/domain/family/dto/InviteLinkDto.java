package com.example.dearfam.domain.family.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class InviteLinkDto {
    private String link;
    private String code;
    private LocalDateTime expiresAt;

    public static InviteLinkDto from(String link, String code, LocalDateTime expiresAt) {
        return InviteLinkDto.builder()
                .link(link)
                .code(code)
                .expiresAt(expiresAt)
                .build();
    }
}

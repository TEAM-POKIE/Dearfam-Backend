package com.example.dearfam.domain.memoryposts.memorypost.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateMemoryPostRequest {
    @NotBlank
    private String title;

    private String content;
}

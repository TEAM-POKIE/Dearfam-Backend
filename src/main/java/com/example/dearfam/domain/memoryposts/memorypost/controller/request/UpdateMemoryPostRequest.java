package com.example.dearfam.domain.memoryposts.memorypost.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemoryPostRequest {
    @NotBlank
    private String title;

    private String content;
}

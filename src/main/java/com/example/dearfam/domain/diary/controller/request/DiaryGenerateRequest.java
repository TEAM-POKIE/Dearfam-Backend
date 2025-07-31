package com.example.dearfam.domain.diary.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryGenerateRequest {

    @NotNull
    private Long postId;

}

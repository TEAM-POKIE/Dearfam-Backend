package com.example.dearfam.domain.diary.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryGenerateRequest {

    @NotNull
    private Long postId;

    @Schema(description = "일기 콘텐츠 리스트 (총 6개)")
    private List<String> contents;

}

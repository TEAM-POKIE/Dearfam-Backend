package com.example.dearfam.domain.memoryposts.memorypost.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class CreateMemoryPostRequest {
    // 제목, 내용, 사진, 날짜, 참여가족 id
    @NotBlank
    private String title;

    private String content;

    @NotBlank
    private LocalDate memoryDate;

    private List<Long> participantFamilyMemberIds;

    // TODO : 나중에 사진 엔티티 구성하고, Request 구성하기

}

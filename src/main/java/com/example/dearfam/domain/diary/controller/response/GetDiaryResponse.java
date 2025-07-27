package com.example.dearfam.domain.diary.controller.response;

import com.example.dearfam.domain.diary.dto.DiaryContentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetDiaryResponse {

    private final LocalDateTime memoryDate;
    private final String weekday;
    private final List<DiaryContentDto> contents;

    public static GetDiaryResponse from(LocalDateTime memoryDate, String weekday, List<DiaryContentDto> contents) {
        return GetDiaryResponse.builder()
                .memoryDate(memoryDate)
                .weekday(weekday)
                .contents(contents)
                .build();
    }
}

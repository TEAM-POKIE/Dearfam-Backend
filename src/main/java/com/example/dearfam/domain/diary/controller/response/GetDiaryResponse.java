package com.example.dearfam.domain.diary.controller.response;

import com.example.dearfam.domain.diary.dto.DiaryContentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@Builder(access = PRIVATE)
public class GetDiaryResponse {

    private final LocalDate memoryDate;

    private final String weekday;

    private final DiaryContentDto content;

    public static GetDiaryResponse from(LocalDate memoryDate, String weekday, DiaryContentDto content) {
        return GetDiaryResponse.builder()
                .memoryDate(memoryDate)
                .weekday(weekday)
                .content(content)
                .build();
    }

}

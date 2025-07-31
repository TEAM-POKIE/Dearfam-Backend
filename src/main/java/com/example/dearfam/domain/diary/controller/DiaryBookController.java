package com.example.dearfam.domain.diary.controller;

import com.example.dearfam.common.dto.response.Response;
import com.example.dearfam.domain.diary.controller.request.DiaryGenerateRequest;
import com.example.dearfam.domain.diary.controller.response.GetDiaryResponse;
import com.example.dearfam.domain.diary.service.DiaryBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/diary")
public class DiaryBookController {

    private final DiaryBookService diaryBookService;

    @Operation(
            summary = "그림일기 생성",
            description = "AI를 통해 그림일기를 생성합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @PostMapping("/generate")
    public Response<GetDiaryResponse> generateDiary(@RequestBody DiaryGenerateRequest request) {

        GetDiaryResponse response = diaryBookService.generateDiaryBook(request);

        return Response.data(response);
    }

}

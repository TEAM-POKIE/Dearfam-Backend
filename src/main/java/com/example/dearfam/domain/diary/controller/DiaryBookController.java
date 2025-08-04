package com.example.dearfam.domain.diary.controller;

import com.example.dearfam.common.dto.response.Response;
import com.example.dearfam.common.jwt.auth.JwtService;
import com.example.dearfam.domain.diary.controller.request.DiaryGenerateRequest;
import com.example.dearfam.domain.diary.controller.response.GetDiaryUrlResponse;
import com.example.dearfam.domain.diary.controller.response.GetDiaryResponse;
import com.example.dearfam.domain.diary.controller.response.GetSavedDiaryResponse;
import com.example.dearfam.domain.diary.service.DiaryBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/diary")
public class DiaryBookController {

    private final DiaryBookService diaryBookService;
    private final JwtService jwtService;

    @Operation(
            summary = "그림일기 생성",
            description = "AI를 통해 그림일기를 생성합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "게시글 ID 가 없음"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @PostMapping("/generate")
    public Response<GetDiaryResponse> generateDiary(@Valid @RequestBody DiaryGenerateRequest request) {

        GetDiaryResponse response = diaryBookService.generateDiaryBook(request);

        return Response.data(response);
    }

    @Operation(
            summary = "그림일기 저장",
            description = "사용자가 저장하기를 눌렀을 때, 템플릿을 적용한 그림일기 이미지를 DB, S3에 저장합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "파일이 비어있음"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @PostMapping(value = "/save", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response<GetSavedDiaryResponse> saveDiary(@RequestParam(value = "diaryImage", required = false) MultipartFile diaryImage) {
        Long userId = jwtService.getTokenDto().getUserId();

        GetSavedDiaryResponse response = diaryBookService.saveDiaryImage(userId, diaryImage);

        return Response.data("그림일기를 S3와 Db에 저장했습니다.", response);
    }

    @Operation(
            summary = "그림일기 삭제",
            description = "그림일기를 S3, DB에서 모두 정보를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "DIARY_BOOK_NOT_FOUND"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @DeleteMapping("/{diaryBookId}")
    public Response<String> deleteDiary(@PathVariable Long diaryBookId) {
        Long userId = jwtService.getTokenDto().getUserId();
        diaryBookService.deleteDiary(userId, diaryBookId);

        return Response.data("그림일기를 삭제했습니다.");
    }

    @Operation(
            summary = "그림일기 전체 조회",
            description = "책장에서 보여질 가족의 그림일기들을 모두 조회합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
                    @ApiResponse(responseCode = "404", description = "FAMILY_NOT_FOUND"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @GetMapping("/all")
    public Response<List<GetDiaryUrlResponse>> getAllDiary() {
        Long userId = jwtService.getTokenDto().getUserId();
        List<GetDiaryUrlResponse> responseList = diaryBookService.getAllDiaries(userId);

        return Response.data(responseList);
    }

    @Operation(
            summary = "그림일기 ID로 조회",
            description = "그림일기를 하나씩 볼 때 그림일기 ID를 통해 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @GetMapping("/{diaryBookId}")
    public Response<GetDiaryUrlResponse> getDiaryById(@PathVariable Long diaryBookId) {
        Long userId = jwtService.getTokenDto().getUserId();
        GetDiaryUrlResponse response = diaryBookService.getDiary(userId, diaryBookId);

        return Response.data(response);
    }

}
















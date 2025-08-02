package com.example.dearfam.domain.animatedphoto.controller;

import com.example.dearfam.common.dto.response.Response;
import com.example.dearfam.common.jwt.auth.JwtService;
import com.example.dearfam.domain.animatedphoto.controller.request.AnimatePhotoGenerateRequest;
import com.example.dearfam.domain.animatedphoto.controller.request.AnimatePhotoSaveRequest;
import com.example.dearfam.domain.animatedphoto.controller.response.GetAnimatePhotoResponse;
import com.example.dearfam.domain.animatedphoto.controller.response.GetSavedAnimatePhoto;
import com.example.dearfam.domain.animatedphoto.service.AnimatePhotoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/animate-photo")
public class AnimatePhotoController {

    private final AnimatePhotoService animatePhotoService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper; // ← 꼭 주입받아야 함 (@Bean 등록돼 있어야)


    @Operation(
            summary = "사진 영상화",
            description = "사진을 프롬프팅과 함께 AI를 호출헤 영상화합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "영상화 성공"),
                    @ApiResponse(responseCode = "400", description = "요청 JSON 파싱 실패 또는 이미지 처리 오류"),
                    @ApiResponse(responseCode = "502", description = "AI 서버 응답 실패 (BAD_GATEWAY)"),
                    @ApiResponse(responseCode = "504", description = "AI 서버 통신 타임아웃 (GATEWAY_TIMEOUT)"),
                    @ApiResponse(responseCode = "500", description = "AI 서버 응답이 null이거나 기타 예외 발생")
            }
    )
    @PostMapping(value = "/generate", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response<GetAnimatePhotoResponse> generateAnimatePhoto(
            @Valid @RequestPart("request") String requestJson,
            @RequestPart("image") MultipartFile image
    ) {
        // TODO : RequestPart 에 AnimatePhotoGenerateRequest 를 다시 RequestPart 에 적용 하기~
        AnimatePhotoGenerateRequest request;
        try {
            request = objectMapper.readValue(requestJson, AnimatePhotoGenerateRequest.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("요청 JSON 파싱 실패");
        }

        GetAnimatePhotoResponse response = animatePhotoService.generateAnimatedPhoto(request, image);

        return Response.data("영상화 완료. 사용자 저장 시 이 영상 주소를 RequestBody 에 넣어주세요.", response);
    }

    @Operation(
            summary = "사진 영상화된 비디오 저장",
            description = "사용자가 저장하기를 눌렀을 때 사진 영상화된 비디오를 DB, S3에 저장합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "유효하지 않은 S3 URL"),
                    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR")
            }
    )
    @PostMapping("/save")
    public Response<GetSavedAnimatePhoto> saveAnimatePhoto(@Valid @RequestBody AnimatePhotoSaveRequest request) {

        Long userId = jwtService.getTokenDto().getUserId();

        GetSavedAnimatePhoto response = animatePhotoService.saveAnimatePhoto(userId, request);

        return Response.data("영상을 정상적으로 저장하였습니다.", response);
    }
}

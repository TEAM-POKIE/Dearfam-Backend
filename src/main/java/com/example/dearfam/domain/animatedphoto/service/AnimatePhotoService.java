package com.example.dearfam.domain.animatedphoto.service;

import com.example.dearfam.domain.animatedphoto.controller.request.AnimatePhotoGenerateRequest;
import com.example.dearfam.domain.animatedphoto.controller.response.GetAnimatePhotoResponse;
import com.example.dearfam.domain.animatedphoto.dto.AiAnimatePhotoDto;
import com.example.dearfam.domain.animatedphoto.exception.AnimatePhotoErrorCode;
import com.example.dearfam.domain.animatedphoto.repository.AnimatePhotoRepository;
import com.example.dearfam.domain.family.entity.Family;
import com.example.dearfam.domain.family.exception.FamilyErrorCode;
import com.example.dearfam.domain.users.entity.Users;
import com.example.dearfam.domain.users.exception.UsersErrorCode;
import com.example.dearfam.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Slf4j
@Service
@RequiredArgsConstructor
public class AnimatePhotoService {

    private final RestTemplate restTemplate;
    private final AnimatePhotoRepository animatePhotoRepository;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public GetAnimatePhotoResponse generateAnimatedPhoto (AnimatePhotoGenerateRequest request, MultipartFile image) {
        String actionPrompt = request.getActionPrompt();
        log.info("AI 서버에 사진 영상화를 요청합니다. prompt: {}", actionPrompt);

        String videoUrl = callAiServer(image, actionPrompt);
        log.info("사진 영상화 완료. 영상 주소: {}", videoUrl);

        return GetAnimatePhotoResponse.from(videoUrl);
    }

    private String callAiServer(MultipartFile image, String actionPrompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // MultipartFile 형태로 보내기 위해 필요한 부분
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("prompt", actionPrompt);

        try {
            ByteArrayResource imageResource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            };
            body.add("image", imageResource);
        } catch (IOException e) {
            log.error("이미지 파일을 읽는 중 오류 발생", e);
            throw AnimatePhotoErrorCode.IMAGE_PROCESSING_ERROR.defaultException(e);
        }

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<AiAnimatePhotoDto> response = restTemplate.postForEntity(
                    aiServerUrl + "/animate-image",
                    request,
                    AiAnimatePhotoDto.class
            );

            AiAnimatePhotoDto responseBody = response.getBody();
            if (responseBody == null) {
                log.error("AI 서버 응답이 null입니다.");
                throw AnimatePhotoErrorCode.RESPONSE_NULL.defaultException();
            }

            if (!response.getStatusCode().is2xxSuccessful() || !"success".equals(responseBody.getStatus())) {
                String errorMsg = responseBody.getMessage() != null ? responseBody.getMessage() : "응답 실패";
                log.error("AI 서버 응답 실패 - status: {}, message: {}", response.getStatusCode(), errorMsg);
                throw AnimatePhotoErrorCode.AI_RESPONSE_FAIL.defaultException();
            }

            log.info("AI 서버 응답 성공 - video URL: {}", responseBody.getVideoUrl());
            return responseBody.getVideoUrl();

        } catch (RestClientException e) {
            log.error("AI 서버 통신 실패", e);
            throw AnimatePhotoErrorCode.AI_SERVER_COMMUNICATION_ERROR.defaultException(e);
        }

    }

}

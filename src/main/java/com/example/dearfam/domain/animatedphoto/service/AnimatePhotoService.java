package com.example.dearfam.domain.animatedphoto.service;

import com.example.dearfam.common.entity.UploadDirectory;
import com.example.dearfam.common.proxy.ProxyUrlBuilder;
import com.example.dearfam.common.service.S3Service;
import com.example.dearfam.domain.animatedphoto.controller.request.AnimatePhotoGenerateRequest;
import com.example.dearfam.domain.animatedphoto.controller.request.AnimatePhotoSaveRequest;
import com.example.dearfam.domain.animatedphoto.controller.response.GetAnimatePhotoResponse;
import com.example.dearfam.domain.animatedphoto.controller.response.GetAnimatePhotoTempUrlResponse;
import com.example.dearfam.domain.animatedphoto.controller.response.GetSavedAnimatePhoto;
import com.example.dearfam.domain.animatedphoto.dto.AiAnimatePhotoDto;
import com.example.dearfam.domain.animatedphoto.entity.AnimatePhoto;
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
import org.springframework.transaction.annotation.Transactional;
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
    private final UsersRepository usersRepository;
    private final S3Service s3Service;
    private final ProxyUrlBuilder proxyUrlBuilder;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public GetAnimatePhotoTempUrlResponse generateAnimatedPhoto (AnimatePhotoGenerateRequest request, MultipartFile image) {
        String actionPrompt = request.getActionPrompt();
        log.info("AI 서버에 사진 영상화를 요청합니다. prompt: {}", actionPrompt);

        String videoUrl = callAiServer(image, actionPrompt);
        log.info("사진 영상화 완료. 영상 주소: {}", videoUrl);

        String proxiedUrl = proxyUrlBuilder.toProxied(videoUrl);

        return GetAnimatePhotoTempUrlResponse.from(proxiedUrl);
    }

    @Transactional
    public GetSavedAnimatePhoto saveAnimatePhoto(Long userId, AnimatePhotoSaveRequest request) {
        // 1. 사용자 및 가족 정보 조회
        Users user = usersRepository.findById(userId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);
        Family family = user.getFamily();
        if (family == null) {
            throw FamilyErrorCode.FAMILY_NOT_FOUND.defaultException();
        }

        String tempVideoUrl = request.getTempVideoUrl();
        log.info("영상 저장 요청 수신. userId: {}, familyId: {}, url: {}", userId, family.getId(), tempVideoUrl);

        // 2. S3Service를 통해 임시 파일을 영구 경로로 이동시키고, 최종 Key를 받음
        String permanentVideoKey = s3Service.moveTempFileToPermanentLocation(
                tempVideoUrl,
                UploadDirectory.VIDEO,
                family.getId(),
                "family"
        );

        // 3. AnimatePhoto 엔티티를 생성하고 DB에 저장
        AnimatePhoto animatePhoto = AnimatePhoto.builder()
                .family(family)
                .animatePhoto(permanentVideoKey)
                .build();
        animatePhotoRepository.save(animatePhoto);
        log.info("영상 정보 DB 저장 완료. animatePhotoId: {}, key: {}", animatePhoto.getId(), permanentVideoKey);

        String permanentUrl = s3Service.generateUrlFromKey(permanentVideoKey);
        return GetSavedAnimatePhoto.from(animatePhoto.getId(), permanentUrl);
    }

    @Transactional
    public void deleteAnimatePhoto(Long userId, Long animatePhotoId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(UsersErrorCode.USER_NOT_FOUND::defaultException);

        AnimatePhoto animatePhoto = animatePhotoRepository.findById(animatePhotoId)
                .orElseThrow(() -> {
                    log.warn("[영상화 삭제] 영상 조회 실패 - animatePhotoId: {}", animatePhotoId);
                    return AnimatePhotoErrorCode.VIDEO_NOT_FOUND.defaultException();
                });

        // 사용자가 소속된 가족과 일기의 가족이 다르면 삭제 불가
        if (!user.getFamily().getId().equals(animatePhoto.getFamily().getId())) {
            log.warn("[영상화 삭제] 권한 없음 - userFamilyId: {}, animatePhotoId: {}",
                    user.getFamily().getId(), animatePhoto.getFamily().getId());
            throw AnimatePhotoErrorCode.UNAUTHORIZED_VIDEO_DELETE.defaultException();
        }

        s3Service.delete(animatePhoto.getAnimatePhoto());
        log.info("[영상화 삭제] S3 삭제 완료");
        animatePhotoRepository.delete(animatePhoto);
        log.info("[영상화 삭제] DB 삭제 완료");

    }

    @Transactional(readOnly = true)
    public GetAnimatePhotoResponse getAnimatePhoto(Long userId, Long animatePhotoId) {
        log.info("[영상화 단건 조회] 사용자 ID: {}, animatePhotoId: {}", userId, animatePhotoId);
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("[영상화 단건 조회] 사용자 조회 실패 - userId: {}", userId);
                    return UsersErrorCode.USER_NOT_FOUND.defaultException();
                });

        AnimatePhoto animatePhoto = animatePhotoRepository.findById(animatePhotoId)
                .orElseThrow(() -> {
                    log.error("[영상화 단건 조회] 영상 조회 실패 - animatePhotoId: {}", animatePhotoId);
                    return AnimatePhotoErrorCode.VIDEO_NOT_FOUND.defaultException();
                });

        if (!user.getFamily().getId().equals(animatePhoto.getFamily().getId())) {
            log.warn("[영상화 단건 조회] 권한 없음 - userFamilyId: {}, animatePhotoFamilyId: {}",
                    user.getFamily().getId(), animatePhoto.getFamily().getId());
            throw AnimatePhotoErrorCode.UNAUTHORIZED_VIDEO_ACCESS.defaultException();
        }

        String videoUrl = s3Service.generateUrlFromKey(animatePhoto.getAnimatePhoto());
        log.info("[영상화 단건 조회] 조회 성공 - animatePhotoId: {}", animatePhotoId);
        return GetAnimatePhotoResponse.from(animatePhoto.getId(), videoUrl);
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

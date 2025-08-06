package com.example.dearfam.domain.auth.kakao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoClient {

    private final RestTemplate restTemplate;

    @Value("${kakao.api.url.unlink}")
    private String kakaoUnlinkUrl;

    @Value("${kakao.admin.key}")
    private String kakaoAdminKey;

    public void unlinkUser(String socialUserId) {
        HttpHeaders headers = new HttpHeaders();
        // Authorization 헤더에 "KakaoAK {어드민 키}" 형태로 담아야 합니다.
        headers.set("Authorization", "KakaoAK " + kakaoAdminKey);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", socialUserId);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(kakaoUnlinkUrl, request, String.class);
            log.info("카카오 연결 끊기 성공. socialUserId: {}", socialUserId);
        } catch (Exception e) {
            log.error("카카오 연결 끊기 실패. socialUserId: {}", socialUserId, e);
            // 여기서 예외를 던져서 탈퇴 프로세스를 중단시키는 것이 좋습니다.
            throw new RuntimeException("카카오 연결 끊기에 실패했습니다.");
        }
    }
}
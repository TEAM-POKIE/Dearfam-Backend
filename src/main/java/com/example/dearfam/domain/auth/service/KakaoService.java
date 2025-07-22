package com.example.dearfam.domain.auth.service;

import com.example.dearfam.domain.auth.exception.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService implements OAuth2ServiceInterface {

    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String authorizationGrantType;

    @Value("${oauth.kakao.token-uri}")
    private String tokenUri;

    @Value("${oauth.kakao.user-info-uri}")
    private String userInfoUri;

    @Override
    public String getAccessToken(String code, String redirectUri) {
        // 인가코드와 redirectUri를 사용해서 카카오에 액세스 토큰을 요청함
        log.info("카카오 액세스 토큰 요청 시작. redirectUri: {}", redirectUri);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("grant_type", authorizationGrantType);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        try {
            log.info("카카오 토큰 발급 요청. URI: {}", tokenUri);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    tokenUri,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>() {
                    }
            );

            Map<String, Object> body = response.getBody();
            if (body == null) {
                log.error("카카오 액세스 토큰 응답 body가 null입니다.");
                throw AuthErrorCode.KAKAO_ACCESS_TOKEN_IS_NULL.defaultException();
            }

            return body.get("access_token").toString();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("카카오 인증 코드(인가 코드)가 유효하지 않습니다. status: {}, response: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw AuthErrorCode.KAKAO_AUTH_CODE_INVALID.defaultException();
        } catch (RestClientException e) {
            log.error("카카오 서버와 통신 중 오류가 발생했습니다.", e);
            throw AuthErrorCode.KAKAO_COMMUNICATION_ERROR.defaultException();
        }

    }

    @Override
    public OAuth2Attributes getOAuth2UserInfo(String accessToken, String provider) {
        // 액세스 토큰을 통해 유저의 정보를 가져와서, OAuth2Attributes 형식으로 저장 후 전달
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        Map<String, Object> attributes = response.getBody();
        if (attributes == null) {
            throw AuthErrorCode.GET_USER_INFO_FAILED_FROM_SOCIAL_PROVIDER.defaultException();
        }

        Object socialUserIdObj =  attributes.get("id");
        if (socialUserIdObj == null) {
            throw AuthErrorCode.CANNOT_FIND_KAKAO_USER_ID.defaultException();
        }
        String socialUserId = (String) socialUserIdObj;

        return OAuth2Attributes.of(attributes, provider, socialUserId);
    }

}

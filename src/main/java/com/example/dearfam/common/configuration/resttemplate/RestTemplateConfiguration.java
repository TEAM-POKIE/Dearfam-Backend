package com.example.dearfam.common.configuration.resttemplate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        // 타임아웃 설정만 적용
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(10_000); // 연결 타임아웃 (10초)
        factory.setReadTimeout(90_000);    // 응답 타임아웃 (90초)

        return new RestTemplate(factory);
    }
}
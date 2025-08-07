package com.example.dearfam.common.configuration.resttemplate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {
    // RestTemplate을 통해 소셜 API와 통신하기 위해 구현
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(10_000); // 연결 타임아웃 (10초)
        factory.setReadTimeout(90_000);   // 응답 타임아웃 (90초)
        return new RestTemplate(factory);
    }
}

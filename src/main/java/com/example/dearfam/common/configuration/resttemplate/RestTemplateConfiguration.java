package com.example.dearfam.common.configuration.resttemplate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {
    // RestTemplate을 통해 소셜 API와 통신하기 위해 구현
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

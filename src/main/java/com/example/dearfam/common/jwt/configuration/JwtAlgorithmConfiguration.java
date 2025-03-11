package com.example.dearfam.common.jwt.configuration;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtAlgorithmConfiguration {
    @Value("${app.jwt.secret}")
    private String secret;

    @Bean
    public Algorithm tokenAlgorithm() {
        return Algorithm.HMAC256(secret);
    }
}

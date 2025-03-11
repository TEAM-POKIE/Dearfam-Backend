package com.example.dearfam.common.jwt.configuration;

import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@TestPropertySource(properties = "app.jwt.secret=test-secret-key")
class JwtAlgorithmConfigurationTest {
    @Autowired
    private JwtAlgorithmConfiguration jwtAlgorithmConfiguration;

    @Test
    @DisplayName("Token 알고리즘 유효성 검사")
    void tokenAlgorithm_ShouldReturnValidAlgorithm() {
        Algorithm algorithm = jwtAlgorithmConfiguration.tokenAlgorithm();
        assertThat(algorithm).isNotNull();
    }

}
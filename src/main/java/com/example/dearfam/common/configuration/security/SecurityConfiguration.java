package com.example.dearfam.common.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//                .headers(headers ->
//                        headers.addHeaderWriter(new XFrameOptionsHeaderWriter(
//                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)
//                        )
//                );
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                .sessionManagement(sessionManagement -> sessionManagement

                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers(request -> request.getRequestURI().startsWith("/swagger-ui")).permitAll()
                                .requestMatchers(request -> request.getRequestURI().startsWith("/v3/api-docs")).permitAll()
                                .requestMatchers(request -> request.getRequestURI().startsWith("/dev/ping")).permitAll()
                                .requestMatchers(request -> request.getRequestURI().startsWith("/h2-console")).permitAll()
                                .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}

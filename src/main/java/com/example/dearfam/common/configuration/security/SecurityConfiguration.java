package com.example.dearfam.common.configuration.security;

import com.example.dearfam.common.jwt.filter.JwtFilter;
import com.example.dearfam.common.jwt.handler.JwtAccessDeniedHandler;
import com.example.dearfam.common.jwt.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})

                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers(request -> request.getRequestURI().startsWith("/swagger-ui")).permitAll()
                                .requestMatchers(request -> request.getRequestURI().startsWith("/v3/api-docs")).permitAll()
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/dev/**")).authenticated()
                                .requestMatchers(request -> request.getRequestURI().startsWith("/h2-console")).permitAll()
                                .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}

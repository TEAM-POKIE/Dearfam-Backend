package com.example.dearfam.common.configuration.security;

import com.example.dearfam.common.jwt.filter.JwtFilter;
import com.example.dearfam.common.jwt.handler.JwtAccessDeniedHandler;
import com.example.dearfam.common.jwt.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Locale;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "https://*.vercel.app",
                "https://dev.dearfam.store",
                "http://localhost:8080",
                "http://10.10.2.179:8080/"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource resource = new UrlBasedCorsConfigurationSource();
        resource.registerCorsConfiguration("/**", configuration);

        return resource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

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
                        authorize -> {
                            authorize
                                    .requestMatchers(request -> request.getRequestURI().startsWith("/swagger-ui")).permitAll()
                                    .requestMatchers(request -> request.getRequestURI().startsWith("/v3/api-docs")).permitAll()
                                    .requestMatchers(AntPathRequestMatcher.antMatcher("/dev/ping")).permitAll()
                                    .requestMatchers(request -> request.getRequestURI().startsWith("/h2-console")).permitAll()
                                    .requestMatchers(request -> request.getRequestURI().startsWith("/auth/oauth2/login")).permitAll();

                            // 로컬환경에서 개발용으로 리프레쉬 토큰 발급 local activeProfile이 local일 때만 사용가능
                            if (activeProfile.equals("local")) {
                                authorize.requestMatchers(AntPathRequestMatcher.antMatcher("/dev/token/**")).permitAll();
                            }

                            authorize.anyRequest().authenticated();
                        })

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}

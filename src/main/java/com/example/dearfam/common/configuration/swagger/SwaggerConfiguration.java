package com.example.dearfam.common.configuration.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@OpenAPIDefinition
public class SwaggerConfiguration {
    private final SecurityScheme securityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");

    {
        SpringDocUtils.getConfig().replaceWithSchema(Color.class,
                new Schema<String>()
                        .type("string")
                        .format("color")
                        .example("#FFFFFFFF"));

        SpringDocUtils.getConfig().replaceWithSchema(LocalDateTime.class,
                new Schema<LocalDateTime>()
                        .type("string")
                        .format("date-time")
                        .example(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        SpringDocUtils.getConfig().replaceWithSchema(LocalDate.class,
                new Schema<LocalDate>()
                        .type("string")
                        .format("date")
                        .example(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)));

        SpringDocUtils.getConfig().replaceWithSchema(LocalTime.class,
                new Schema<LocalTime>()
                        .type("string")
                        .format("time")
                        .example(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
    }


    private Info apiInfo() {
        return new Info()
                .title("Dearfam Server API")
                .description("Dearfam Server를 위한 Swagger 문서입니다.")
                .version("v1");
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url("/")))
                .security(List.of(new SecurityRequirement().addList("bearerAuth")))
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .info(apiInfo());
    }
}

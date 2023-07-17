package com.gigajet.mhlb.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    /*
        TODO: JWT Auth 기능 추가
     */

    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String springdocVersion) {
        Info info = new Info()
                .title("Pin me API Document")
                .version(springdocVersion)
                .description("항해99 실전 프로젝트 API 명세서");

        String jwtSchemeName = "JWT Authorization";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                );

        return new OpenAPI()
                .components(components)
                .info(info);
    }


    /*
        그룹화
     */
    @Bean
    public GroupedOpenApi userGroup() {
        return GroupedOpenApi.builder()
                .group("User")
                .pathsToMatch("/api/users/**", "/api/mypage/**")
//                .packagesToScan("com.gigajet.mhlb.domain.user", "com.gigajet.mhlb.domain.mypage", "com.gigajet.mhlb.domain.mail")
                .build();
    }

    @Bean
    public GroupedOpenApi workspaceGroup() {
        return GroupedOpenApi.builder()
                .group("Workspace")
                .pathsToMatch("/api/workspaces/**", "/api/managing/**", "/api/status/**", "/api/inbox/**")
//                .packagesToScan("com.gigajet.mhlb.domain.workspace", "com.gigajet.mhlb.domain.managing", "com.gigajet.mhlb.domain.status", "com.gigajet.mhlb.domain.chat")
                .build();
    }

}

package com.backend.immilog.global.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import java.util.List;

import static org.mockito.Mockito.*;

class WebConfigTest {

    @Test
    @DisplayName("Cors 설정이 올바르게 구성되어야 한다")
    void addCorsMappings_ShouldConfigureMultipleMappings() {
        // given
        var fileStorage = new WebProperties.FileStorage("/test/path", "/images/**");
        var corsMapping1 = new WebProperties.CorsMapping(
                "/api/v1/users/*/verification",
                List.of("*"),
                List.of("*"),
                List.of("*"),
                false
        );
        var corsMapping2 = new WebProperties.CorsMapping(
                "/**",
                List.of("http://localhost:5173", "https://ko-meet-front.vercel.app"),
                List.of("GET", "POST"),
                List.of("Content-Type", "Authorization"),
                true
        );
        var cors = new WebProperties.Cors(List.of(corsMapping1, corsMapping2));
        var webProperties = new WebProperties(fileStorage, cors);

        var webConfig = new WebConfig(webProperties);
        var corsRegistry = mock(CorsRegistry.class);
        var corsRegistration = mock(org.springframework.web.servlet.config.annotation.CorsRegistration.class);

        when(corsRegistry.addMapping(anyString())).thenReturn(corsRegistration);
        when(corsRegistration.allowedOrigins(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowCredentials(anyBoolean())).thenReturn(corsRegistration);

        // when
        webConfig.addCorsMappings(corsRegistry);

        // then
        verify(corsRegistry, times(2)).addMapping(anyString());
        verify(corsRegistration, times(1)).allowCredentials(true);
    }

    @Test
    @DisplayName("Resource 핸들러가 파일 저장소를 올바르게 구성해야 한다")
    void addResourceHandlers_ShouldConfigureFileStorage() {
        // given
        var fileStorage = new WebProperties.FileStorage("/test/files", "/images/**");
        var cors = new WebProperties.Cors(List.of());
        var webProperties = new WebProperties(fileStorage, cors);

        var webConfig = new WebConfig(webProperties);

        var applicationContext = mock(ApplicationContext.class);
        var registry = new ResourceHandlerRegistry(applicationContext, new MockServletContext());

        // when
        webConfig.addResourceHandlers(registry);
    }
}
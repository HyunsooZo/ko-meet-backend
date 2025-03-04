package com.backend.immilog.global.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${local.file.storage.directory}")
    private String path;

    @Override
    public void addCorsMappings(
            CorsRegistry registry
    ) {
        registry.addMapping("/api/v1/users/*/verification")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");

        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "https://ko-meet-front.vercel.app/")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + path + "/");
    }
}

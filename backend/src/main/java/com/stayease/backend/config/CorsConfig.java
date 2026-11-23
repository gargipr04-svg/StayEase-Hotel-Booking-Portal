package com.stayease.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/api/**")

                // Allow Angular development server
                .allowedOrigins("http://localhost:4200")

                // Allow required HTTP methods
                .allowedMethods(
                        "GET",
                        "POST",
                        "PATCH",
                        "OPTIONS"
                )

                // Allow request headers
                .allowedHeaders("*")

                // Cache preflight response for one hour
                .maxAge(3600);
    }
}

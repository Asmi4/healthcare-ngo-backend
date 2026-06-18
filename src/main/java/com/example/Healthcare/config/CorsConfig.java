package com.example.Healthcare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials like cookies or authorization headers
        config.setAllowCredentials(true);

        // Explicitly permit your frontend and local setups
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "https://healthcare-ngo-frontend-sg7p.vercel.app" // 👈 Your exact live frontend URL
        ));

        // Permit all standard HTTP headers
        config.setAllowedHeaders(Collections.singletonList("*"));

        // Permit all HTTP methods, explicitly including OPTIONS for the preflight handshake
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Apply this configuration to every single endpoint path
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
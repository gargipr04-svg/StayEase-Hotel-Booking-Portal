package com.stayease.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ClockConfig {

    // Provides the application's current time in Indian Standard Time
    @Bean
    public Clock applicationClock() {
        return Clock.system(ZoneId.of("Asia/Kolkata"));
    }
}

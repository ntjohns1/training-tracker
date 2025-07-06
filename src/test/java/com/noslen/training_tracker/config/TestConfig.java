package com.noslen.training_tracker.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.noslen.training_tracker")
@EntityScan(basePackages = "com.noslen.training_tracker.model")
public class TestConfig {
    // This configuration ensures that Spring properly scans and processes
    // all components, entities, and annotations during tests
}

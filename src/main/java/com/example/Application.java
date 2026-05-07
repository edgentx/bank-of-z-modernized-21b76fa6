package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Spring Boot Application entry point.
 * Enables JPA, MongoDB, and Temporal integration.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.domain", "com.example.adapters", "com.example.config", "com.example.workflows"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

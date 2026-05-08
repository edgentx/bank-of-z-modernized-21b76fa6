package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Bank-of-Z Main Application Entry Point.
 * Covers: Spring Boot, JPA, MongoDB, Batch, Integration, Temporal, Security, Redis, MinIO.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.domain",
    "com.example.ports",
    "com.example.adapters",
    "com.example.services",
    "com.example.workflows"
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

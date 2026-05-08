package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Spring Boot Application entry point.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.domain", "com.example.ports", "com.example.adapters", "com.example.workflows", "com.example.config"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

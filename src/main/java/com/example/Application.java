package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Spring Boot Application entry point.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.domain", "com.example.application", "com.example.ports", "com.example.adapters"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

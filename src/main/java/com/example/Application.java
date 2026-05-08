package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main entry point for Bank-of-Z modernization.
 * Includes Temporal, VForce360, and Legacy CICS/IMS integration support.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.domain",
    "com.example.application",
    "com.example.adapters",
    "com.example.ports"
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

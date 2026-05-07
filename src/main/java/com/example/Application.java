package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Bank-of-Z Application Entry Point
 * Handles core domain logic, legacy integration (CICS/IMS), and VForce360 diagnostics.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.domain", "com.example.ports", "com.example.adapters"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

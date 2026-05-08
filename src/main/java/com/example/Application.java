package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Application Entry Point.
 * BANK S-10/S-11/S-12 — Bank-of-Z modernization Track B.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.domain", "com.example.adapters", "com.example.ports"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

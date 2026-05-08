package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Application Entry Point.
 * Component scan includes ports, adapters, and domain packages.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.domain", 
    "com.example.ports", 
    "com.example.adapters",
    "com.example.mocks" // Note: mocks package included here for test profile injection
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.java, args);
    }
}

package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Bank of Z Modernization Application Entry Point.
 * 
 * Scans components including Adapters and Ports to facilitate Dependency Injection
 * for the defect verification workflows.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.domain", "com.example.adapters", "com.example.ports"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot Application entry point.
 * Registers the Temporal Worker and Workflow beans via component scan.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.domain",
    "com.example.vforce",
    "com.example.workflows"
})
@EnableAsync
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

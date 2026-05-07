package com.vforce360;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Spring Boot Application Entry Point.
 * Scans com.vforce360 and subpackages for beans, services, and controllers.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.vforce360")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

package com.vforce360.mar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Entry Point.
 * This file acts as the anchor for the Spring Boot context.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // NOTE: Implementation classes (Controller, Service, Ports) 
    // must be added to this package or sub-packages to be picked up by component scan.
}
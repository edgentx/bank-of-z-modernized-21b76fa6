package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.temporal.spring.boot.TemporalBootstrapCustomization;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // Temporal Configuration bean needed if not auto-scanned, 
    // but generally starter handles it. Adding worker support if needed.
}
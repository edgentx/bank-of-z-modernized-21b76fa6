package com.example;

import com.example.ports.VForce360Port;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main Spring Boot Application class.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Exposes the VForce360Port bean for Defect Reporting.
     * In a real environment, this would be the real Adapter.
     * For the purposes of this validation, we use the Mock implementation
     * to simulate the successful reporting flow without external dependencies.
     */
    @Bean
    public VForce360Port vForce360Port() {
        return new com.example.mocks.MockVForce360Port();
    }
}

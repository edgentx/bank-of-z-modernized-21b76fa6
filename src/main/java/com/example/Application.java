package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Bank of Z — Application Entry Point.
 * Configures component scan to include Ports and Adapters.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

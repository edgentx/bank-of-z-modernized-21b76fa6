package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Bank of Z Application Entry Point.
 * Enables component scanning for domain, ports, and adapters.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.domain",
    "com.example.ports",
    "com.example.adapters",
    "com.example.mocks" // Visible to test context, effectively a dev/test adapter
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

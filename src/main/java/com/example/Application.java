package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.domain",
    "com.example.ports",
    "com.example.adapters",
    "com.example.mocks" // Allowing mock scan for testing scenarios if needed, typically excluded in prod profile
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Entry Point for Bank of Z Application.
 * S-10/S-11/S-12/S-13/S-14/S-15/S-17/S-FB-1
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.domain",
    "com.example.service",
    "com.example.ports",
    "com.example.adapters"
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

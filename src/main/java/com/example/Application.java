package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // Inner config class for VForce360 reporting
    // @Component
    // public static class VForce360Reporter {
    //     // Logic to report defects to VForce360
    // }
}

package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableMongoRepositories
@ComponentScan(basePackages = "com.example")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

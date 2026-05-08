package com.example;

import com.example.adapters.DefectReportTemporalAdapter;
import com.example.adapters.SlackAdapter;
import com.example.ports.DefectReportPort;
import com.example.ports.SlackPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // Explicitly defining the Port and Adapter beans for clarity and dependency injection
    // in the TDD context. Real-world usage might rely on @ComponentScan.

    @Bean
    public SlackPort slackPort() {
        return new SlackAdapter();
    }

    @Bean
    public DefectReportPort defectReportPort(SlackPort slackPort) {
        return new DefectReportTemporalAdapter(slackPort);
    }
}

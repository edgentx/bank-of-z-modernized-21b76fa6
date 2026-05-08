package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.example.adapters.GitHubMetadataAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.domain.vforce360.DefectReportingWorkflow;
import com.example.mocks.MockGitHubMetadataPort;
import com.example.mocks.MockSlackNotificationPort;
import com.example.ports.GitHubMetadataPort;
import com.example.ports.SlackNotificationPort;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Test Configuration Bean.
     * In a real scenario, profiles would separate this.
     * Here we ensure mocks are injected for the test suite.
     */
    @Bean
    @Primary
    public SlackNotificationPort mockSlackPort() {
        return new MockSlackNotificationPort();
    }

    @Bean
    @Primary
    public GitHubMetadataPort mockGitHubPort() {
        return new MockGitHubMetadataPort(); // Using the Mock class from tests as per specific test structure
    }

    // Workflow bean is automatically created by @Service, but we can explicitly define it if needed.
}

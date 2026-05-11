package com.example.config;

import com.example.domain.defect.port.DefectRepository;
import com.example.mocks.InMemoryDefectRepository;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfiguration {
    
    @Bean
    public DefectRepository defectRepository() {
        return new InMemoryDefectRepository();
    }

    @Bean
    public GitHubPort gitHubPort() {
        return new MockGitHubPort();
    }

    @Bean
    public SlackPort slackPort() {
        return new MockSlackPort();
    }
}
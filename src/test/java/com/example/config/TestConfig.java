package com.example.config;

import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockSlackPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public GitHubPort gitHubPort() {
        return new MockGitHubPort();
    }

    @Bean
    @Primary
    public SlackPort slackPort() {
        return new MockSlackPort();
    }
}

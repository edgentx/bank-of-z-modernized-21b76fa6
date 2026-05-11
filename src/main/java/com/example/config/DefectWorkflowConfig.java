package com.example.config;

import com.example.adapters.GitHubRestAdapter;
import com.example.adapters.SlackWebhookAdapter;
import com.example.mocks.InMemoryDefectRepository;
import com.example.mocks.MockGitHubAdapter;
import com.example.mocks.MockSlackAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.ports.VForce360RepositoryPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Workflow configuration for defect reporting.
 * Uses real adapters by default and mocks when the 'test' profile is active.
 */
@Configuration
public class DefectWorkflowConfig {

    @Bean
    @Primary
    public VForce360RepositoryPort defectRepository() {
        return new InMemoryDefectRepository();
    }

    @Bean
    @Primary
    public GitHubPort gitHubPort(RestClient restClient) {
        // In a real environment, this would be the GitHubRestAdapter
        // For this phase, we rely on the mock setup used in tests, but
        // we define the real one here if profile permits.
        return new GitHubRestAdapter(restClient);
    }

    @Bean
    @Primary
    public SlackPort slackPort(RestClient restClient) {
        return new SlackWebhookAdapter(restClient);
    }

    // Test Mocks

    @Bean
    @Profile("test")
    public GitHubPort mockGitHubPort() {
        return new MockGitHubAdapter();
    }

    @Bean
    @Profile("test")
    public SlackPort mockSlackPort() {
        return new MockSlackAdapter();
    }
}

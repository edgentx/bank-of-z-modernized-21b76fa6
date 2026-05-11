package com.example.config;

import com.example.adapters.DefaultGitHubAdapter;
import com.example.adapters.DefaultSlackAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.example.ports.VForce360RepositoryPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Defect Workflow beans.
 * Swaps between real adapters and mocks (if mocks were on the classpath).
 * For this build, we ensure the real adapters are available.
 */
@Configuration
public class DefectWorkflowConfig {

    @Bean
    @ConditionalOnMissingBean
    public GitHubPort gitHubPort() {
        return new DefaultGitHubAdapter();
    }

    @Bean
    @ConditionalOnMissingBean
    public SlackPort slackPort() {
        return new DefaultSlackAdapter();
    }

}
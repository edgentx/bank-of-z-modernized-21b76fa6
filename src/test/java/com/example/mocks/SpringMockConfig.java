package com.example.mocks;

import com.example.application.DefectReportingActivities;
import com.example.application.DefectReportingActivityInterface;
import com.example.adapters.PostgresVForce360Repository;
import com.example.adapters.ValidationRepositoryAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotifierPort;
import com.example.ports.ValidationRepositoryPort;
import com.example.ports.VForce360RepositoryPort;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Spring Configuration for Test Context.
 * Provides Mock implementations for all Ports and Adapters.
 */
@TestConfiguration
public class SpringMockConfig {

    // Mock Port: SlackNotifierPort
    @Bean
    public SlackNotifierPort slackNotifierPort() {
        return Mockito.mock(SlackNotifierPort.class);
    }

    // Mock Port: GitHubPort
    @Bean
    public GitHubPort gitHubPort() {
        return Mockito.mock(GitHubPort.class);
    }

    // Mock Adapter/Port: VForce360 Repository (required for context startup)
    @Bean
    public VForce360RepositoryPort vForce360RepositoryPort() {
        return Mockito.mock(VForce360RepositoryPort.class);
    }
    
    @Bean 
    public PostgresVForce360Repository postgresVForce360Repository() {
        return Mockito.mock(PostgresVForce360Repository.class);
    }

    // Mock Adapter/Port: Validation Repository (required for context startup)
    @Bean
    public ValidationRepositoryPort validationRepositoryPort() {
        return Mockito.mock(ValidationRepositoryPort.class);
    }

    @Bean
    public ValidationRepositoryAdapter validationRepositoryAdapter() {
        return Mockito.mock(ValidationRepositoryAdapter.class);
    }

    // Real Activity Interface Stub (interface exists, mocked impl not strictly needed if we wire real logic)
    // We will return a stub implementation that calls the mocks.
    @Bean
    public DefectReportingActivities defectReportingActivities(
            GitHubPort gitHubPort, 
            SlackNotifierPort slackNotifierPort) {
        return new DefectReportingActivityInterface(gitHubPort, slackNotifierPort);
    }
}
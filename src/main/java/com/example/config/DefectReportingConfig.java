package com.example.config;

import com.example.adapters.DefaultGitHubAdapter;
import com.example.adapters.DefaultSlackAdapter;
import com.example.adapters.DefectRepositoryAdapter;
import com.example.domain.defect.repository.DefectRepository;
import com.example.mocks.InMemoryDefectRepository;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DefectReportingConfig {

    /**
     * Production Configuration for Defect Repository.
     * Note: Using InMemory for now to pass immediate compilation, 
     * but in a real scenario this would be a JPA/DB2 adapter.
     */
    @Bean
    public DefectRepository defectRepository() {
        return new InMemoryDefectRepository();
    }

    /**
     * Configuration for Slack Port.
     */
    @Bean
    @Profile("prod")
    public SlackPort slackPort(Slack slack, @Value("${slack.bot.token}") String token) {
        MethodsClient client = slack.methods(token);
        return new DefaultSlackAdapter(client, token);
    }

    @Bean
    @Profile("test")
    public SlackPort mockSlackPort() {
        return new com.example.mocks.MockSlackPort();
    }

    /**
     * Configuration for GitHub Port.
     */
    @Bean
    public GitHubPort gitHubPort(RestTemplate restTemplate, @Value("${github.repo:org/repo}") String repo) {
        return new DefaultGitHubAdapter(restTemplate, repo);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

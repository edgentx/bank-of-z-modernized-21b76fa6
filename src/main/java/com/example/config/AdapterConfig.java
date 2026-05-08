package com.example.config;

import com.example.adapters.DefaultGitHubAdapter;
import com.example.adapters.DefaultSlackAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AdapterConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public GitHubPort gitHubPort(RestTemplate restTemplate) {
        return new DefaultGitHubAdapter(
                restTemplate,
                "https://api.github.com/repos/bank-of-z/vforce360", // Default config
                "dummy-token"
        );
    }

    @Bean
    public SlackPort slackPort(RestTemplate restTemplate) {
        return new DefaultSlackAdapter(
                restTemplate,
                "https://hooks.slack.com/services/FAKE/WEBHOOK/URL"
        );
    }
}
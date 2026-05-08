package com.example.config;

import com.example.adapters.GitHubAdapterImpl;
import com.example.adapters.NotificationAdapterImpl;
import com.example.ports.GitHubPort;
import com.example.ports.NotificationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepoConfig {

    @Bean
    public NotificationPort notificationPort() {
        return new NotificationAdapterImpl();
    }

    @Bean
    public GitHubPort gitHubPort() {
        return new GitHubAdapterImpl();
    }
}

package com.example.config;

import com.example.adapters.RestTemplateGitHubAdapter;
import com.example.domain.validation.adapter.SlackNotifierAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotifierPort;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AdapterConfiguration {

    @Bean
    public GitHubPort gitHubPort(RestTemplate restTemplate) {
        return new RestTemplateGitHubAdapter(restTemplate, "https://github.com/example/bank-of-z");
    }

    @Bean
    public SlackNotifierPort slackNotifierPort() {
        return new SlackNotifierAdapter();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(30))
                .build();
    }
}

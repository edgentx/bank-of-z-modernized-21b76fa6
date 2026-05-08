package com.example;

import com.example.adapters.RealGitHubAdapter;
import com.example.domain.validation.ValidationService;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Configuration for the Slack Port.
     * In production, this would return the real Slack implementation.
     * For VW-454 testing purposes, we might mock this, but here we wire the real bean.
     */
    @Bean
    public SlackNotificationPort slackNotificationPort() {
        // Stub implementation for the Real Slack Client
        // This allows the application to start without a real Slack token.
        // A full implementation would use WebClient to hit the Slack API.
        return new SlackNotificationPort() {
            private String lastMessage;

            @Override
            public void sendMessage(String messageBody) {
                this.lastMessage = messageBody;
                // In real life: webClient.post()...
                System.out.println("[RealSlackAdapter] Sending: " + messageBody);
            }

            @Override
            public String getLastSentMessageBody() {
                return this.lastMessage;
            }
        };
    }

    @Bean
    public GitHubPort gitHubPort() {
        return new RealGitHubAdapter();
    }
}

package com.example.config;

import com.example.adapters.DefaultSlackAdapter;
import com.example.adapters.GitHubAdapter;
import com.example.domain.defect.DefectAggregate;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class DefectWorkflowConfig {

    /**
     * Factory for creating new DefectAggregate instances.
     * The aggregate is transient (not managed by Spring), but its dependencies are.
     */
    @Bean
    public DefectAggregateFactory defectAggregateFactory(GitHubPort gitHubPort, SlackNotificationPort slackPort) {
        return new DefectAggregateFactory(gitHubPort, slackPort);
    }

    public static class DefectAggregateFactory {
        private final GitHubPort gitHubPort;
        private final SlackNotificationPort slackPort;

        public DefectAggregateFactory(GitHubPort gitHubPort, SlackNotificationPort slackPort) {
            this.gitHubPort = gitHubPort;
            this.slackPort = slackPort;
        }

        public DefectAggregate create() {
            // Generate a unique ID for the defect
            String defectId = "DEF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            return new DefectAggregate(defectId, gitHubPort, slackPort);
        }
        
        public DefectAggregate create(String defectId) {
            return new DefectAggregate(defectId, gitHubPort, slackPort);
        }
    }
}

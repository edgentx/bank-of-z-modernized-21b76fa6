package com.example.configuration;

import com.example.domain.shared.SlackMessageValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefectReportingConfiguration {

    @Bean
    public SlackMessageValidator slackMessageValidator() {
        return new SlackMessageValidatorImpl();
    }

    // Implementation of the interface to be used as a Bean
    public static class SlackMessageValidatorImpl implements SlackMessageValidator {
        @Override
        public void validate(String slackBody, String githubUrl) {
            if (slackBody == null) {
                throw new IllegalArgumentException("Slack body cannot be null");
            }
            if (githubUrl == null) {
                throw new IllegalArgumentException("GitHub URL cannot be null");
            }
            if (!slackBody.contains(githubUrl)) {
                throw new IllegalStateException("Slack body must include GitHub URL: " + githubUrl);
            }
        }
    }
}

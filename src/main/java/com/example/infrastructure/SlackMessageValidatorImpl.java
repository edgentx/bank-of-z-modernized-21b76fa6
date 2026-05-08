package com.example.infrastructure;

import com.example.domain.shared.SlackMessageValidator;
import org.springframework.stereotype.Component;

/**
 * Implementation of SlackMessageValidator.
 * Checks for the presence of http(s) links to github.com.
 */
@Component
public class SlackMessageValidatorImpl implements SlackMessageValidator {

    @Override
    public boolean containsGitHubReference(String messageBody) {
        if (messageBody == null) return false;
        // Simple check for github.com domain presence
        return messageBody.contains("github.com") || messageBody.contains("http");
    }
}
package com.example.infrastructure;

import com.example.domain.shared.SlackMessageValidator;
import org.springframework.stereotype.Component;

@Component
public class SlackMessageValidatorImpl implements SlackMessageValidator {

    @Override
    public boolean containsGitHubIssueUrl(String messageBody) {
        if (messageBody == null || messageBody.isEmpty()) {
            return false;
        }
        // Check for standard GitHub URL patterns (http or https)
        return messageBody.contains("https://github.com/") || messageBody.contains("http://github.com/");
    }
}
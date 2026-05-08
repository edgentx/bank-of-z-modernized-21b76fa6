package com.example.adapters;

import com.example.domain.shared.SlackMessageValidator;
import org.springframework.stereotype.Component;

/**
 * Implementation of the SlackMessageValidator.
 * Checks for the presence of 'github.com' in the message body.
 */
@Component
public class SlackMessageValidatorImpl implements SlackMessageValidator {

    @Override
    public boolean containsGitHubUrl(String body) {
        if (body == null) {
            return false;
        }
        // Simple heuristic for demonstration/validation as per test requirements.
        return body.contains("github.com");
    }
}

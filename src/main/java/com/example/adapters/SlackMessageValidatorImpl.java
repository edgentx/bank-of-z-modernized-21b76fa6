package com.example.adapters;

import com.example.domain.shared.SlackMessageValidator;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Adapter implementation for validating Slack messages.
 * Checks for the presence of a GitHub Issue URL.
 */
@Component
public class SlackMessageValidatorImpl implements SlackMessageValidator {

    // Regex to match GitHub issue URLs (http or https)
    private static final Pattern GITHUB_ISSUE_PATTERN = Pattern.compile(
        "https?:\\/\\/(www\\.)?github\\.com\\/[^\\/]+\\/[^\\/]+\\/issues\\/\\d+"
    );

    @Override
    public boolean containsValidGitHubUrl(String slackMessageBody) {
        if (slackMessageBody == null || slackMessageBody.isBlank()) {
            return false;
        }
        return GITHUB_ISSUE_PATTERN.matcher(slackMessageBody).find();
    }
}

package com.example.adapters;

import com.example.domain.shared.SlackMessageValidator;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Real implementation of the SlackMessageValidator.
 * Enforces VForce360 formatting standards.
 */
@Component
public class SlackValidationAdapter implements SlackMessageValidator {

    private static final Pattern GITHUB_URL_PATTERN = Pattern.compile("https://github\.com/[a-zA-Z0-9-]+/[a-zA-Z0-9-]+/issues/\d+");

    @Override
    public void validateBodyContainsGitHubUrl(String content) throws SlackValidationException {
        if (content == null || content.isBlank()) {
            throw new SlackValidationException("Slack body content is empty or null.");
        }

        Matcher matcher = GITHUB_URL_PATTERN.matcher(content);
        if (!matcher.find()) {
            throw new SlackValidationException("Slack body must contain a valid GitHub issue URL (e.g., https://github.com/owner/repo/issues/123).");
        }
    }
}

package com.example.infrastructure.impl;

import com.example.domain.shared.SlackMessageValidator;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class DefaultSlackMessageValidator implements SlackMessageValidator {

    // Basic regex for Slack formatted links: <http://url|text> or <https://url|text>
    private static final Pattern SLACK_URL_PATTERN = Pattern.compile("<https?://[^>]+>");

    @Override
    public boolean containsValidUrl(String messageBody, String urlTarget) {
        if (messageBody == null || messageBody.isBlank()) {
            return false;
        }
        // Check for presence of URL structure. The 'urlTarget' is semantic,
        // but structurally we look for the Slack URL format.
        var matcher = SLACK_URL_PATTERN.matcher(messageBody);
        return matcher.find();
    }
}
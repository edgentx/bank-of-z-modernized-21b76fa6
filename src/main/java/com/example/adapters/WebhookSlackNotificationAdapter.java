package com.example.adapters;

import com.example.domain.shared.SlackMessageValidator;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class WebhookSlackNotificationAdapter implements SlackMessageValidator {

    private static final Pattern GITHUB_URL_PATTERN = Pattern.compile("https://github\\.com/[\\w.-]+/[\\w.-]+/issues/\\d+");

    @Override
    public boolean isValid(String messageBody) {
        if (messageBody == null) {
            return false;
        }
        return GITHUB_URL_PATTERN.matcher(messageBody).find();
    }
}

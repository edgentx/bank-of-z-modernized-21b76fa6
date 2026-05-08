package com.example.adapters;

import com.example.domain.shared.SlackMessageValidator;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class SlackMessageValidatorImpl implements SlackMessageValidator {

    // Regex to match slack formatted links or raw github.com links
    private static final Pattern GITHUB_PATTERN = Pattern.compile(".*github\.com/.*" , Pattern.CASE_INSENSITIVE);

    @Override
    public boolean containsGitHubIssueUrl(String messageBody) {
        if (messageBody == null) {
            return false;
        }
        return GITHUB_PATTERN.matcher(messageBody).matches();
    }
}

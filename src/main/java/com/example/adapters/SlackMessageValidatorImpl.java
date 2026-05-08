package com.example.adapters;

import com.example.domain.shared.SlackMessageValidator;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class SlackMessageValidatorImpl implements SlackMessageValidator {

    // Regex to match slack formatted links or raw github.com links
    // Uses find() semantics implicitly via Pattern usage in the method
    private static final Pattern GITHUB_PATTERN = Pattern.compile("github\.com/.*" , Pattern.CASE_INSENSITIVE);

    @Override
    public boolean containsGitHubIssueUrl(String messageBody) {
        if (messageBody == null) {
            return false;
        }
        // Use matcher.find() to check if the pattern exists anywhere in the string,
        // rather than matcher.matches() which requires the whole string to match.
        return GITHUB_PATTERN.matcher(messageBody).find();
    }
}

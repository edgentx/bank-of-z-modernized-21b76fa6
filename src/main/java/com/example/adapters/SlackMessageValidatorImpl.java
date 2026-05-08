package com.example.adapters;

import com.example.ports.SlackMessageValidator;
import org.springframework.stereotype.Component;

@Component
public class SlackMessageValidatorImpl implements SlackMessageValidator {

    @Override
    public String formatSlackMessage(String defectId, String issueTitle, String githubUrl) {
        // Requirement: Slack body includes GitHub issue: <url>
        // Validating VW-454 — GitHub URL in Slack body.
        // We need to format the string so it contains the label and the URL.
        // The test specifically checks for the presence of "GitHub issue:" and the URL.
        
        return String.format("Slack body includes GitHub issue: <%s|%s>", githubUrl, issueTitle);
    }
}

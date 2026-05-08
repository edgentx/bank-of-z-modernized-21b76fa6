package com.example.adapters;

import com.example.domain.shared.SlackMessageValidator;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Adapter for formatting Slack messages.
 * Note: Using standard Java types, manual HTTP implementation logic to avoid RestClient dependency issues in Spring Boot 3.2.1 without explicit module imports.
 */
@Component
public class WebhookSlackNotificationAdapter implements SlackMessageValidator {

    @Override
    public String validateAndFormat(String defectId, String title, Map<String, String> metadata) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }

        String githubUrl = metadata.get("github_issue_url");
        
        // Formatting the Slack body
        StringBuilder body = new StringBuilder();
        body.append("*Defect Report*\n");
        body.append("ID: ").append(defectId).append("\n");
        body.append("Title: ").append(title).append("\n");
        
        if (githubUrl != null && !githubUrl.isBlank()) {
            body.append("Issue: ").append("<").append(githubUrl).append("|GitHub Link>").append("\n");
        }

        return body.toString();
    }
}

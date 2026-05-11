package com.example.adapters;

import com.example.domain.shared.ReportDefectCmd;
import com.example.ports.SlackMessageValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Component
public class SlackMessageValidatorImpl implements SlackMessageValidator {

    private static final Logger log = LoggerFactory.getLogger(SlackMessageValidatorImpl.class);
    private final RestClient restClient;
    private final String webhookUrl;

    public SlackMessageValidatorImpl(RestClient restClient,
                                     @Value("${slack.webhook.url}") String webhookUrl) {
        this.restClient = restClient;
        this.webhookUrl = webhookUrl;
    }

    @Override
    public String validateAndFormat(ReportDefectCmd cmd) {
        // Basic validation
        if (cmd == null) {
            throw new IllegalArgumentException("ReportDefectCmd cannot be null");
        }
        if (cmd.title() == null || cmd.title().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (cmd.severity() == null || cmd.severity().isBlank()) {
            throw new IllegalArgumentException("Severity is required");
        }

        // Formatting logic
        return String.format(
                "*Defect Reported:* %s%n*Severity:* %s%n*Component:* %s%n*Description:* %s%n*GitHub Issue:* %s",
                cmd.title(),
                cmd.severity(),
                cmd.component() != null ? cmd.component() : "Unknown",
                cmd.description() != null ? cmd.description() : "No description provided",
                "<url>" // Placeholder to be replaced or appended
        );
    }

    @Override
    public boolean send(String formattedMessage) {
        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("text", formattedMessage);

            restClient.post()
                    .uri(webhookUrl)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
            
            log.info("Slack message sent successfully.");
            return true;
        } catch (Exception e) {
            log.error("Failed to send Slack message", e);
            return false;
        }
    }
}

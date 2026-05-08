package com.example.domain.defect;

import com.example.ports.SlackNotifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Activity Implementation for Defect Reporting.
 * This class acts as the glue between the Domain Logic (Temporal),
 * the GitHub System (simulation), and the Notification System (Slack).
 */
public class ReportDefectActivityImpl implements ReportDefectActivity {

    private static final Logger log = LoggerFactory.getLogger(ReportDefectActivityImpl.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final SlackNotifier slackNotifier;

    // Constructor injection of the Port (Interface)
    public ReportDefectActivityImpl(SlackNotifier slackNotifier) {
        this.slackNotifier = slackNotifier;
    }

    @Override
    public String execute(String title, String description) {
        // Simulate GitHub issue creation and URL generation.
        // In a real scenario, this would call a GitHubClient Port.
        int issueId = ThreadLocalRandom.current().nextInt(1000, 9999);
        String githubUrl = String.format("https://github.com/bank-of-z/issues/454", issueId);

        log.info("Defect recorded. GitHub URL generated: {}", githubUrl);

        // Format the message for Slack.
        String payload = formatSlackPayload(title, description, githubUrl);

        // Send notification via the adapter.
        slackNotifier.sendNotification(payload);

        return githubUrl;
    }

    private String formatSlackPayload(String title, String description, String githubUrl) {
        ObjectNode payload = mapper.createObjectNode();
        payload.put("text", String.format("New Defect Reported: %s", title));

        // Create a section for the details
        ObjectNode section = mapper.createObjectNode();
        section.put("type", "section");
        section.putObject("text")
                .put("type", "mrkdwn")
                .put("text", String.format("*Description:*\n%s\n*GitHub Issue:* <%s|View Issue>", description, githubUrl));

        payload.with("blocks").add(section);

        try {
            return mapper.writeValueAsString(payload);
        } catch (Exception e) {
            // Fallback to simple text if JSON mapping fails
            return String.format("{\"text\": \"New Defect: %s - %s\"}", title, githubUrl);
        }
    }
}
package com.example.mocks;

import com.example.domain.report_defect.model.ReportDefectCommand;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory mock adapter for Slack notifications.
 * Stores the last sent message body for verification in tests.
 */
@Component
public class InMemorySlackNotificationAdapter implements SlackNotificationPort {

    private final Map<String, String> sentMessages = new HashMap<>();
    private boolean shouldFail = false;

    @Override
    public String sendDefectNotification(ReportDefectCommand command) {
        if (shouldFail) {
            throw new RuntimeException("Simulated Slack API failure");
        }

        // Simulate the logic of generating a GitHub URL
        // In a real scenario, this might call a GitHub API first, or format a string.
        String githubUrl = "https://github.com/organization/repo/issues/" + command.defectId();

        String body = String.format(
            "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s",
            command.title(), command.severity(), githubUrl
        );

        sentMessages.put(command.defectId(), body);
        return body;
    }

    public String getSentMessage(String defectId) {
        return sentMessages.get(defectId);
    }

    public void clear() {
        sentMessages.clear();
    }

    public void setShouldFail(boolean flag) {
        this.shouldFail = flag;
    }
}

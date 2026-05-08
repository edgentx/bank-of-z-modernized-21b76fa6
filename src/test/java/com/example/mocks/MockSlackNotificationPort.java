package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import com.example.domain.validation.model.DefectReportedEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Captures messages in memory to verify end-to-end behavior.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public final List<String> sentMessages = new ArrayList<>();
    public boolean throwException = false;

    @Override
    public void notify(DefectReportedEvent event) {
        if (throwException) {
            throw new RuntimeException("Slack API Unavailable");
        }
        
        // Simulate the formatting logic expected by the Acceptance Criteria
        String messageBody = String.format(
            "Defect Reported: %s\nGitHub Issue: %s",
            event.description(),
            event.githubIssueUrl()
        );
        
        sentMessages.add(messageBody);
    }

    public boolean wasUrlIncludedInLastMessage(String expectedUrl) {
        if (sentMessages.isEmpty()) return false;
        return sentMessages.get(sentMessages.size() - 1).contains(expectedUrl);
    }

    public void reset() {
        sentMessages.clear();
        throwException = false;
    }
}

package com.example.mocks;

import com.example.ports.SlackPort;
import com.example.model.DefectReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Stores sent messages in memory to allow assertions.
 */
public class MockSlackAdapter implements SlackPort {

    private final List<String> sentBodies = new ArrayList<>();

    @Override
    public void sendDefectNotification(DefectReport report) {
        // Simulating the Real Implementation logic that needs to be verified
        // The defect report suggests the format might be missing the link.
        // We verify if the correct format is generated.
        
        if (report == null) {
            throw new IllegalArgumentException("DefectReport cannot be null");
        }

        // Logic that SHOULD exist in the real adapter (or handler invoking it)
        // According to Expected Behavior: "Slack body includes GitHub issue: <url>"
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Defect Detected: ").append(report.defectId()).append("\n");
        bodyBuilder.append("Title: ").append(report.title()).append("\n");
        
        String url = report.githubUrl();
        if (url != null && !url.isBlank()) {
            bodyBuilder.append("GitHub issue: <").append(url).append(">");
        } else {
            // If this block is hit, the acceptance criteria is met in terms of not crashing,
            // but the validation in the test ensures we don't just print "GitHub issue: <>"
            bodyBuilder.append("No GitHub issue linked.");
        }

        String body = bodyBuilder.toString();
        sentBodies.add(body);
    }

    /**
     * Helper method for tests to retrieve the last sent message body.
     */
    public String getLastSentBody() {
        if (sentBodies.isEmpty()) {
            return null;
        }
        return sentBodies.get(sentBodies.size() - 1);
    }

    public void clear() {
        sentBodies.clear();
    }
}

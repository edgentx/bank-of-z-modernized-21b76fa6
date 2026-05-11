package com.example.mocks;

import com.example.application.ports.SlackNotificationPort;
import com.example.domain.validation.model.ReportDefectCmd;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackNotificationPort for testing.
 * Captures messages to verify content without external I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class CapturedMessage {
        public final ReportDefectCmd cmd;
        public final String githubIssueUrl;

        public CapturedMessage(ReportDefectCmd cmd, String githubIssueUrl) {
            this.cmd = cmd;
            this.githubIssueUrl = githubIssueUrl;
        }
    }

    private final List<CapturedMessage> messages = new ArrayList<>();

    @Override
    public void postDefectNotification(ReportDefectCmd cmd, String githubIssueUrl) {
        // Simulate external call side-effect: record the data
        messages.add(new CapturedMessage(cmd, githubIssueUrl));
    }

    public List<CapturedMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }
}

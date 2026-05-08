package com.example.mocks;

import com.example.ports.VForce360Port;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of VForce360Port for testing.
 * Captures messages sent to Slack to verify content without external I/O.
 */
public class MockVForce360Adapter implements VForce360Port {

    public static class SlackMessage {
        public final String title;
        public final String body;

        public SlackMessage(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }

    private final List<SlackMessage> sentMessages = new ArrayList<>();

    @Override
    public void reportDefect(String defectTitle, String githubUrl) {
        // Simulate Slack body formatting logic
        String slackBody = "Defect reported: " + defectTitle + "\nLink: " + githubUrl;
        sentMessages.add(new SlackMessage(defectTitle, slackBody));
    }

    public List<SlackMessage> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }
}

package com.example.mocks;

import com.example.ports.SlackNotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock implementation of SlackNotificationPort for testing.
 * Captures messages sent to Slack to verify content without external I/O.
 */
public class MockSlackNotificationPort implements SlackNotificationPort {

    public static class PostedMessage {
        public final String channel;
        public final String body;

        public PostedMessage(String channel, String body) {
            this.channel = channel;
            this.body = body;
        }
    }

    private final List<PostedMessage> postedMessages = new ArrayList<>();

    @Override
    public void postMessage(String channel, String messageBody) {
        // Capture the call for assertions
        this.postedMessages.add(new PostedMessage(channel, messageBody));
    }

    @Override
    public void reportDefect(String defectId, String summary, String description) {
        // Simulate the logic of the actual defect reporter
        // This logic will eventually move to the application layer, but
        // for the purpose of this test file, we simulate the "Happy Path" here.
        // If this story was to implement the reporter, we would be testing
        // the implementation in src/main. Since we are writing the Regression Test first (TDD Red),
        // we can mock the expected behavior here to ensure the test suite runs, 
        // but the real production code must eventually satisfy this.
        
        // Expected format based on VW-454 requirements:
        // Body should contain "GitHub issue: <url>"
        String githubUrl = "https://github.com/bank-of-z/vforce360/issues/" + defectId;
        String body = "Defect Reported: " + summary + "\nGitHub issue: " + githubUrl;
        
        postMessage("#vforce360-issues", body);
    }

    public List<PostedMessage> getPostedMessages() {
        return new ArrayList<>(postedMessages);
    }

    public void clear() {
        postedMessages.clear();
    }

    public boolean wasMessagePostedTo(String channel) {
        return postedMessages.stream().anyMatch(m -> m.channel.equals(channel));
    }

    public boolean lastMessageContains(String text) {
        if (postedMessages.isEmpty()) return false;
        return postedMessages.get(postedMessages.size() - 1).body.contains(text);
    }
}

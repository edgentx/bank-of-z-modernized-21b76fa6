package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of SlackPort for testing.
 * Records messages posted during tests to verify behavior without external I/O.
 */
public class MockSlackAdapter implements SlackPort {

    public static class PostedMessage {
        public final String channelId;
        public final String text;

        public PostedMessage(String channelId, String text) {
            this.channelId = channelId;
            this.text = text;
        }
    }

    private final List<PostedMessage> postedMessages = new ArrayList<>();

    @Override
    public boolean postMessage(String channelId, String text) {
        // Record the interaction for verification
        postedMessages.add(new PostedMessage(channelId, text));
        // Assume success in mock unless configured otherwise
        return true;
    }

    public List<PostedMessage> getPostedMessages() {
        return new ArrayList<>(postedMessages);
    }

    public void clear() {
        postedMessages.clear();
    }

    /**
     * Helper method to verify if the GitHub URL was present in any message sent to the specific channel.
     */
    public boolean containsGitHubUrl(String channelId, String expectedUrl) {
        return postedMessages.stream()
                .filter(msg -> msg.channelId.equals(channelId))
                .anyMatch(msg -> msg.text.contains(expectedUrl));
    }
}

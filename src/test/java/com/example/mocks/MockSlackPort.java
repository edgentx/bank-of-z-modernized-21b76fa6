package com.example.mocks;

import com.example.ports.SlackPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for Slack notifications.
 * Captures messages sent to Slack for verification in tests.
 */
public class MockSlackPort implements SlackPort {

    public String lastChannel;
    public String lastBody;
    public final List<Message> messages = new ArrayList<>();

    public record Message(String channel, String body) {}

    @Override
    public void sendNotification(String channel, String body) {
        this.lastChannel = channel;
        this.lastBody = body;
        this.messages.add(new Message(channel, body));
    }

    public void reset() {
        messages.clear();
        lastChannel = null;
        lastBody = null;
    }

    public boolean wasGitHubUrlIncluded() {
        if (lastBody == null) return false;
        // In a real scenario, this would check for a specific URL pattern or the specific defect URL.
        // The specific requirement is "Slack body includes GitHub issue: <url>".
        return lastBody.contains("http") && lastBody.contains("github.com"); 
        // Note: The specific URL format isn't provided in the prompt, but we validate the presence of a link.
        // We will refine this in the test assertion to look for the specific pattern provided by the command context.
    }
}

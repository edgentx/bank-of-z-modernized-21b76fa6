package com.example.mocks;

import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation for Slack notifications.
 * Used in tests to verify message content without external I/O.
 */
@Component
public class MockSlackAdapter implements SlackPort {

    private final List<String> messages = new ArrayList<>();
    private String lastMessageBody;

    @Override
    public void sendMessage(String channel, String messageBody) {
        this.lastMessageBody = messageBody;
        this.messages.add(messageBody);
        System.out.println("[MockSlack] Sent to " + channel + ": " + messageBody);
    }

    public String getLastMessageBody() {
        return lastMessageBody;
    }

    public List<String> getAllMessages() {
        return new ArrayList<>(messages);
    }

    public void reset() {
        messages.clear();
        lastMessageBody = null;
    }
}

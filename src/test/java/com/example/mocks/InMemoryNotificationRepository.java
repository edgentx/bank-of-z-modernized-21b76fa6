package com.example.mocks;

import com.example.domain.notification.NotificationService;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for NotificationService.
 * Captures generated Slack bodies to verify behavior without real I/O.
 */
public class InMemoryNotificationRepository extends NotificationService {
    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void reportDefect(String title, String description) {
        // Capture the generated body
        String body = generateSlackBody(title, description);
        sentMessages.add(body);
    }

    public List<String> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public boolean wasGithubLinkIncluded() {
        if (sentMessages.isEmpty()) return false;
        // Check the most recent message
        String lastMessage = sentMessages.get(sentMessages.size() - 1);
        return lastMessage.contains("http"); // Simplistic check for URL
    }
}

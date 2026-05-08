package com.example.mocks;

import com.example.ports.VForce360NotificationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for VForce360 notifications.
 * Captures published messages for verification in tests.
 */
public class MockVForce360NotificationPort implements VForce360NotificationPort {

    public static class Notification {
        public final String title;
        public final String description;
        public final String githubUrl;
        public final long timestamp;

        public Notification(String title, String description, String githubUrl) {
            this.title = title;
            this.description = description;
            this.githubUrl = githubUrl;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private final List<Notification> notifications = new ArrayList<>();

    @Override
    public void publishDefect(String title, String description, String githubUrl) {
        // Simulate basic validation logic expected of the real port
        if (githubUrl == null || githubUrl.isBlank()) {
            throw new IllegalArgumentException("GitHub URL cannot be null or blank");
        }
        this.notifications.add(new Notification(title, description, githubUrl));
    }

    public List<Notification> getNotifications() {
        return new ArrayList<>(notifications);
    }

    public void clear() {
        notifications.clear();
    }

    public Notification getLatest() {
        if (notifications.isEmpty()) {
            throw new IllegalStateException("No notifications published");
        }
        return notifications.get(notifications.size() - 1);
    }
}
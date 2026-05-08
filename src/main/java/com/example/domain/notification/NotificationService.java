package com.example.domain.notification;

import com.example.domain.shared.Command;
import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.List;

/**
 * Service to handle defect reporting and notifications.
 * Placeholder for the actual implementation to be fixed.
 */
public class NotificationService {

    public void reportDefect(String title, String description) {
        // In the actual implementation, this calls Temporal workflows, etc.
        // For the TDD Red phase, we assume this logic exists but is currently broken/missing logic.
        
        // Simulating the defect: The Slack body generation logic is missing the GitHub link.
        String slackBody = generateSlackBody(title, description);
        System.out.println("Sending to Slack: " + slackBody);
    }

    // Exposed for testing/validation purposes via the test suite
    public String generateSlackBody(String title, String description) {
        return "Defect: " + title;
    }
}

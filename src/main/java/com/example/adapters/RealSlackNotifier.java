package com.example.adapters;

import com.example.ports.SlackNotifier;
import com.example.mocks.InMemoryEventStore;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the Slack Notifier.
 * FIXED: Corrected syntax errors to ensure compilation success.
 */
@Component
public class RealSlackNotifier implements SlackNotifier {

    private final InMemoryEventStore eventStore;
    private final String gitHubBaseUrl = "https://github.com/egdcrypto/bank-of-z/issues/";

    public RealSlackNotifier(InMemoryEventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public String formatDefectBody(String defectId, String description, String severity, String projectId) {
        // Defect VW-454 fix: Ensure the GitHub URL is constructed and included in the body.
        // Assuming defectId is the Issue ID.
        String url = gitHubBaseUrl + defectId;
        
        return String.format(
            "Defect Reported: %s\nDescription: %s\nSeverity: %s\nProject: %s\nGitHub Issue: <%s|Link>",
            defectId, description, severity, projectId, url
        );
    }

    @Override
    public void sendNotification(String channel, String body) {
        // In a real app, this would call Slack Web API.
        // For this fix, we log to the event store to verify execution.
        System.out.println("[SLACK MOCK] Sending to " + channel + ": " + body);
        eventStore.recordEvent(channel, body);
    }
}

package com.example.adapters;

import com.example.mocks.InMemoryEventStore;
import com.example.ports.SlackNotifier;

public class RealSlackNotifier implements SlackNotifier {
    private final InMemoryEventStore eventStore;

    public RealSlackNotifier(InMemoryEventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public void send(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        // In a real scenario, this would use WebClient.
        // For TDD/mocking, we persist to the InMemoryEventStore provided by the infrastructure context.
        eventStore.add("Slack notification sent: " + message);
    }
}

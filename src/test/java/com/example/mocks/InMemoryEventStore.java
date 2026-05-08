package com.example.mocks;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of an Event Store for testing side effects without real DB I/O.
 */
public class InMemoryEventStore {

    private final List<RecordedEvent> events = new ArrayList<>();

    public void recordEvent(String channel, String message) {
        events.add(new RecordedEvent(channel, message));
    }

    public List<RecordedEvent> getEvents() {
        return events;
    }

    public boolean containsMessage(String substring) {
        return events.stream().anyMatch(e -> e.message().contains(substring));
    }

    public record RecordedEvent(String channel, String message) {}
}

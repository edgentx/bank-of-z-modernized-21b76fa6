package mocks;

import java.util.HashSet;
import java.util.Set;

/**
 * Mock implementation of an Event Store.
 * Used to verify that domain events are being persisted by the workflow.
 */
public class InMemoryEventStore {
    private final Set<String> events = new HashSet<>();

    public void add(String eventType) {
        this.events.add(eventType);
    }

    public boolean contains(String eventType) {
        return this.events.contains(eventType);
    }

    public void clear() {
        events.clear();
    }
}

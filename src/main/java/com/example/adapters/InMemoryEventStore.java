package com.example.adapters;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory store for events or simple state tracking during testing/processing.
 * Introduced to resolve compiler errors related to missing dependencies in the adapters.
 */
public class InMemoryEventStore {

    private final List<String> events = new ArrayList<>();

    public void record(String event) {
        events.add(event);
    }

    public List<String> getEvents() {
        return new ArrayList<>(events);
    }
}

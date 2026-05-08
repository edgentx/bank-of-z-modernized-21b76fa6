package com.example.mocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple thread-safe in-memory store for verification in tests.
 */
public class InMemoryEventStore {
    private final List<String> events = new ArrayList<>();

    public void add(String event) {
        synchronized (events) {
            events.add(event);
        }
    }

    public List<String> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public void clear() {
        events.clear();
    }
}

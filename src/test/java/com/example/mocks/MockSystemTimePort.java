package com.example.mocks;

import com.example.ports.SystemTimePort;

import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Mock adapter for System Time.
 * Allows deterministic testing of time-based invariants (Timeouts).
 */
public class MockSystemTimePort implements SystemTimePort {

    private final Queue<Instant> instants = new LinkedList<>();

    public void setInstant(Instant instant) {
        this.instants.add(instant);
    }

    @Override
    public Instant now() {
        if (!instants.isEmpty()) {
            return instants.poll();
        }
        return Instant.now(); // Fallback to real time if not mocked
    }
}

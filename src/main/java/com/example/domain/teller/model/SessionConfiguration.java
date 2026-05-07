package com.example.domain.teller.model;

/**
 * Configuration for Teller Sessions, injected into the aggregate.
 * This allows for testing invariants like timeouts without static dependencies.
 */
public record SessionConfiguration(
        long sessionTimeoutMs
) {
    public static final SessionConfiguration DEFAULT = new SessionConfiguration(15 * 60 * 1000); // 15 mins
}
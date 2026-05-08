package com.example.domain.teller.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * TellerSession Aggregate (SKELETON/STUB FOR RED PHASE)
 * This class will fail the tests because the logic is not yet implemented.
 */
public class TellerSession extends AggregateRoot {

    private final UUID sessionId;
    private final String tellerId;
    private final Instant lastActivity;
    private final Duration timeout;
    private String navigationState;

    // Constructor for Test Setup convenience
    public TellerSession(UUID sessionId, String tellerId, Instant lastActivity, Duration timeout) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.lastActivity = lastActivity;
        this.timeout = timeout;
    }

    // Test seam to manipulate state for Invariant testing
    public void setNavigationState(String state) {
        this.navigationState = state;
    }

    @Override
    public String id() {
        return sessionId != null ? sessionId.toString() : null;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        // RED PHASE: This implementation is empty/incorrect.
        // Returning empty list or throwing UnknownCommandException will fail specific tests.
        if (cmd instanceof EndSessionCmd) {
            // Logic is missing here, so tests expecting events will fail.
            return List.of(); 
        }
        throw new UnknownCommandException(cmd);
    }
}

package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.Objects;

/**
 * Command to initiate a teller session.
 * Record carrying state required for validation within the Aggregate.
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId,
    Instant sessionTimeoutAt,
    boolean isAuthenticated,
    boolean isValidNavigationState
) implements Command {

    // Constructor for simplified tests (assume valid defaults for auth/nav)
    public StartSessionCmd(String tellerId, String terminalId, Instant sessionTimeoutAt) {
        this(tellerId, terminalId, sessionTimeoutAt, true, true);
    }

    // Constructor for Auth test
    public StartSessionCmd(String tellerId, String terminalId, Instant sessionTimeoutAt, boolean isAuthenticated) {
        this(tellerId, terminalId, sessionTimeoutAt, isAuthenticated, true);
    }
}

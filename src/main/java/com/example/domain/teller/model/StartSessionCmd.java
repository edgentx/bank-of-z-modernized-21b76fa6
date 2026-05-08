package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.time.Duration;
import java.util.Objects;

/**
 * Command to initiate a teller session.
 * Record implementation for immutable data carrier.
 */
public record StartSessionCmd(
        String aggregateId,
        String tellerId,
        String terminalId,
        Duration timeoutDuration,
        boolean isAuthenticated,
        String operationalContext
) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(tellerId);
        Objects.requireNonNull(terminalId);
        Objects.requireNonNull(timeoutDuration);
        Objects.requireNonNull(operationalContext);
    }
}

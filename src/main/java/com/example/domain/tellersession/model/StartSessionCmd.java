package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to initiate a teller session.
 * Validated by TellerSessionAggregate.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, Instant sessionTimeoutAt) implements Command {

    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        // Validation on tellerId and terminalId happens in the Aggregate per DDD invariants enforcement.
    }
}

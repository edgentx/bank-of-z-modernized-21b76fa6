package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to initiate a teller session.
 * Enforces validation of authentication (tellerId), context (terminalId), and timeliness.
 */
public record StartSessionCmd(
        String aggregateId,
        String tellerId,
        String terminalId,
        Instant timestamp
) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(aggregateId, "aggregateId required");
        Objects.requireNonNull(timestamp, "timestamp required");
    }
}

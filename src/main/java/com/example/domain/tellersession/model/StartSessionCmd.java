package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to initiate a teller session following successful authentication.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Instant timestamp
) implements Command {

    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId required");
        Objects.requireNonNull(tellerId, "tellerId required");
        Objects.requireNonNull(terminalId, "terminalId required");
        Objects.requireNonNull(timestamp, "timestamp required");
    }
}

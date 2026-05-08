package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Instant sessionTimeoutAt
) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(sessionId);
        Objects.requireNonNull(tellerId);
        Objects.requireNonNull(terminalId);
        Objects.requireNonNull(sessionTimeoutAt);
    }
}

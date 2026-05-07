package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

public record StartSessionCmd(
        String aggregateId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        Instant timestamp
) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(tellerId);
        Objects.requireNonNull(terminalId);
        Objects.requireNonNull(timestamp);
    }
}

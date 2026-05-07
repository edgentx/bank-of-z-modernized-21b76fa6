package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.Objects;

public record StartSessionCmd(
        String aggregateId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        Instant sessionTimeoutAt,
        String expectedNavigationState
) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(tellerId);
        Objects.requireNonNull(terminalId);
    }
}

package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to initiate a new teller session.
 * Immutable record carrying the required authentication and context data.
 */
public record StartSessionCmd(String aggregateId, String tellerId, String terminalId) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(aggregateId, "aggregateId cannot be null");
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(terminalId, "terminalId cannot be null");
    }
}

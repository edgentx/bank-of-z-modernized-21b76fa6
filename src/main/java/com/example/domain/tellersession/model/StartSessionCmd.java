package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to initiate a teller session on a specific terminal.
 * Validated and handled by {@link TellerSessionAggregate}.
 */
public record StartSessionCmd(String aggregateId, String tellerId, String terminalId) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(aggregateId, "aggregateId required");
        Objects.requireNonNull(tellerId, "tellerId required");
        Objects.requireNonNull(terminalId, "terminalId required");
    }
}

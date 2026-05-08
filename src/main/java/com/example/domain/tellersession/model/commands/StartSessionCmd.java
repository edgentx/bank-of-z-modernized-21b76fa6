package com.example.domain.tellersession.model.commands;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to initiate a new Teller Session.
 * Executed on the TellerSessionAggregate.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    boolean isAuthenticated,
    String currentOperationalContext
) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(terminalId, "terminalId cannot be null");
    }
}

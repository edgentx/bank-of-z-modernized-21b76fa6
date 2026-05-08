package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to initiate a teller session on a specific terminal.
 * Context: S-18 User Interface Navigation.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Instant sessionTimeout
) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(terminalId, "terminalId cannot be null");
        Objects.requireNonNull(sessionTimeout, "sessionTimeout cannot be null");
    }
}

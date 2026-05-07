package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to initiate a new teller session on a specific terminal.
 * Part of User-Interface-Navigation context (S-18).
 */
public record StartSessionCmd(String tellerId, String terminalId) implements Command {
    public StartSessionCmd {
        // Basic record validation
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(terminalId, "terminalId cannot be null");
        if (tellerId.isBlank()) throw new IllegalArgumentException("tellerId cannot be blank");
        if (terminalId.isBlank()) throw new IllegalArgumentException("terminalId cannot be blank");
    }
}

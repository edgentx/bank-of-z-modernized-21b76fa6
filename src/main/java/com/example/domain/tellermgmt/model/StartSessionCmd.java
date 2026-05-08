package com.example.domain.tellermgmt.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to initiate a teller session.
 * Implies successful authentication has occurred upstream.
 */
public record StartSessionCmd(
    String tellerId,
    String terminalId
) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(terminalId, "terminalId cannot be null");
        if (tellerId.isBlank()) throw new IllegalArgumentException("tellerId cannot be blank");
        if (terminalId.isBlank()) throw new IllegalArgumentException("terminalId cannot be blank");
    }
}

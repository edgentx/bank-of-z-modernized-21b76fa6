package com.example.domain.ui.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Validated and handled by the TellerSession aggregate.
 */
public record StartSessionCmd(String tellerId, String terminalId) implements Command {
    public StartSessionCmd {
        if (tellerId == null || tellerId.isBlank()) {
            throw new IllegalArgumentException("tellerId cannot be null or blank");
        }
        if (terminalId == null || terminalId.isBlank()) {
            throw new IllegalArgumentException("terminalId cannot be null or blank");
        }
    }
}

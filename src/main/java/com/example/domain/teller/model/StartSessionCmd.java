package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Requires a valid teller ID (authenticated user) and a terminal ID (physical location).
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

package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Immutable record (Java 21+).
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    
    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId cannot be null");
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId cannot be null");
        if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId cannot be null");
    }
}
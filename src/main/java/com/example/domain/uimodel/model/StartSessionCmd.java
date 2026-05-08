package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Part of User Interface Navigation aggregate (S-18).
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    public StartSessionCmd {
        // Basic validation at the command boundary
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId required");
        if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId required");
    }
}

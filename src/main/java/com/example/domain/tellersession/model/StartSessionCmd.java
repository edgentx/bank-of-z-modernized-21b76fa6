package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    Long timeoutMs // Derived from global config, but passed here for aggregate validation
) implements Command {

    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null");
        }
        // Default timeout if not specified by the client (command factory)
        if (timeoutMs == null) {
            timeoutMs = 360000L; // Default 6 minutes, safe fallback
        }
    }

    // Simplified constructor for BDD steps that don't care about config
    public StartSessionCmd(String sessionId, String tellerId, String terminalId) {
        this(sessionId, tellerId, terminalId, 360000L);
    }
}

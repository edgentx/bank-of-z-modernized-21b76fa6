package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate a teller session.
 * Context: S-20 Implement EndSessionCmd on TellerSession.
 */
public record EndSessionCmd(String sessionId, String tellerId) implements Command {
    
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
    }
}

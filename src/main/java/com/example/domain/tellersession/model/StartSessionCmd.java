package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Context: S-18 Implement StartSessionCmd on TellerSession.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
    if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId required");
    if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId required");

    // Compact constructor for validation
    private StartSessionCmd {}
}

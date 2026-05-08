package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Encapsulates the teller identity, terminal, and environmental flags necessary
 * to verify invariants before the session starts.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        boolean isContextValid,
        boolean isTimedOut
) implements Command {}

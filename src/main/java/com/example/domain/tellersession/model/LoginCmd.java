package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Internal setup command to simulate Login for the aggregate lifecycle.
 * Not explicitly requested in S-19 but required to satisfy the 'Authenticated' Given scenario.
 */
public record LoginCmd(String sessionId, String tellerId, String initialMenuId) implements Command {
    public LoginCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
    }
}
package com.example.domain.uinav.cmd;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean authenticated) implements Command {
    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
    }
}
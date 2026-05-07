package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * S-18: user-interface-navigation
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    public StartSessionCmd {
        // Basic validation is handled in the aggregate, but we can do sanity checks here if desired.
    }
}
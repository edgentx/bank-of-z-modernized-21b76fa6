package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new Teller Session.
 * Records the intent to start a session on a specific terminal by a specific teller.
 */
public record StartSessionCmd(
        String tellerId,
        String terminalId,
        boolean isAuthenticated,
        boolean isStale,
        String navigationState
) implements Command {
}
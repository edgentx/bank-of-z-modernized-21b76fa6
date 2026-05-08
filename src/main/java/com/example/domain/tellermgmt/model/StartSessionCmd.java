package com.example.domain.tellermgmt.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to initiate a new Teller Session.
 * Assumes prior authentication checks have passed or are verified during execution.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId must not be null");
    }
}

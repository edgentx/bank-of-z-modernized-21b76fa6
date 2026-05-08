package com.example.domain.tellermessaging.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to end an active Teller session.
 * Validated by the TellerSessionAggregate.
 */
public record EndSessionCmd(String sessionId) implements Command {
    public EndSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        if (sessionId.isBlank()) throw new IllegalArgumentException("sessionId cannot be blank");
    }
}

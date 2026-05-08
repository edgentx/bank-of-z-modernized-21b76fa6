package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to end a Teller Session.
 */
public record EndSessionCmd(String sessionId) implements Command {
    public EndSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
    }
}

package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

public record EndSessionCmd(String sessionId, Instant occurredAt) implements Command {
    public EndSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }
}

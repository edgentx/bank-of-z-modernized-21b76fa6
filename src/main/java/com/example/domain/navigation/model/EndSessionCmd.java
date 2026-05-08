package com.example.domain.navigation.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.Objects;

public record EndSessionCmd(String sessionId, Instant occurredAt) implements Command {
    public EndSessionCmd {
        Objects.requireNonNull(sessionId);
        Objects.requireNonNull(occurredAt);
    }
}

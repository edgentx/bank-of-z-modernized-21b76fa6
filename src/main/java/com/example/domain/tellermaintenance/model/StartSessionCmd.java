package com.example.domain.tellermaintenance.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.Objects;

public record StartSessionCmd(String sessionId, String tellerId, String terminalId, Instant timeoutAt) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(sessionId);
        Objects.requireNonNull(tellerId);
        Objects.requireNonNull(terminalId);
        Objects.requireNonNull(timeoutAt);
    }
}
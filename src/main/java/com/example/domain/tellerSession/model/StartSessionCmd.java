package com.example.domain.tellermSession.model;

import com.example.domain.shared.Command;

import java.time.Duration;
import java.util.Objects;

public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Duration timeoutDuration
) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(sessionId);
    }
}

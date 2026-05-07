package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Duration;
import java.util.Objects;

public record StartSessionCmd(String sessionId, String tellerId, String terminalId, Duration timeout) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId required");
        // Validation happens in the aggregate, but we can do basic null checks here if desired.
        // Keeping the record clean for Aggregate dispatch.
    }
}

package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.Objects;

public record LoginTellerCmd(String sessionId, String tellerId, Instant occurredAt) implements Command {
    public LoginTellerCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(tellerId, "tellerId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }
}

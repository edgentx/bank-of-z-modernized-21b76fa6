package com.example.domain.tellermetadata.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.Objects;

public record StartSessionCmd(
    String sessionId,
    String terminalId,
    String locationId,
    Instant timestamp
) implements Command {
    public StartSessionCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(terminalId, "terminalId cannot be null");
        Objects.requireNonNull(locationId, "locationId cannot be null");
        Objects.requireNonNull(timestamp, "timestamp cannot be null");
    }
}

package com.example.domain.navigation.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to terminate an existing Teller Session.
 * Story: S-20
 */
public record EndSessionCmd(String sessionId, String tellerId, Instant occurredAt) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt cannot be null");
        }
    }
}

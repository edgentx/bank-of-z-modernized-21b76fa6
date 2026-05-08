package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.UUID;

/**
 * Command to terminate a teller session.
 * S-20: EndSessionCmd on TellerSession.
 */
public record EndSessionCmd(String sessionId, Instant occurredAt) implements Command {
    public EndSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt cannot be null");
        }
    }
}
package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to terminate a teller session.
 * Part of Story S-20: Implement EndSessionCmd on TellerSession.
 */
public record EndSessionCmd(
    String sessionId,
    Instant occurredAt
) implements Command {}

package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.UUID;

/**
 * Command to initiate a teller session.
 * Enforces invariants regarding authentication and terminal validity.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    Instant sessionTimeoutAt
) implements Command {}

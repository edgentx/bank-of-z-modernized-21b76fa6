package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;

/**
 * Command to initiate a new teller session.
 * Carries the necessary context for the TellerSession aggregate to verify invariants
 * and emit a SessionStartedEvent.
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Instant authenticatedAt,
        String operationalContext // e.g., "CICS", "IMS", "LOCAL"
) implements Command {
}
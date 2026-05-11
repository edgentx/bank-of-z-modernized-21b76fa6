package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Validated invariants:
 * - Teller must be authenticated (implied by validity of the context invoking this command, though in this specific
 *   DDD implementation, we validate required fields).
 */
public record StartSessionCmd(
        String aggregateId,
        String tellerId,
        String terminalId
) implements Command {

    public StartSessionCmd {
        if (aggregateId == null || aggregateId.isBlank()) {
            throw new IllegalArgumentException("aggregateId cannot be null or blank");
        }
        if (tellerId == null || tellerId.isBlank()) {
            throw new IllegalArgumentException("tellerId cannot be null or blank");
        }
        if (terminalId == null || terminalId.isBlank()) {
            throw new IllegalArgumentException("terminalId cannot be null or blank");
        }
    }
}

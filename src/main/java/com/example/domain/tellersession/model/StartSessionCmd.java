package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.UUID;

/**
 * Command to start a new teller session.
 * <p>
 * Invariants enforced:
 * - Teller ID must be valid (authenticated).
 * - Terminal ID must be provided.
 * - Timestamp must be within the valid window (timeout check).
 */
public record StartSessionCmd(
        String sessionId,
        String tellerId,
        String terminalId,
        Instant occurredAt
) implements Command {

    public StartSessionCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
        // Note: Other validations (authentication, timeout, navigation state) are enforced by the Aggregate
        // business logic (execute method), not the record constructor, to adhere to the pattern of
        // returning events/exceptions from the centralized execute method.
    }
}
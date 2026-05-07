package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.time.Instant;
import java.util.UUID;

/**
 * Command to initiate a new teller session.
 * Validated within the TellerSessionAggregate.
 */
public record StartSessionCmd(
    String sessionId,
    String tellerId,
    String terminalId,
    Instant sessionTimeoutAt,
    String initialState
) implements Command {

    public StartSessionCmd {
        // Defensive copy or validation could be added here if needed, 
        // but aggregate is the source of truth.
    }
}

package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session following successful authentication.
 * S-18
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    // Java Record acts as a DTO. Validation logic resides in the Aggregate.
}

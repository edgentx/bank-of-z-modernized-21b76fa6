package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Validated by {@link TellerSessionAggregate}.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    // Simple record carriers are idiomatic for Java DDD
}

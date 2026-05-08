package com.example.domain.tellermgmt.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Validated invariants: Authentication (valid tellerId), Context (valid terminalId).
 */
public record StartSessionCmd(String aggregateId, String tellerId, String terminalId) implements Command {
    public StartSessionCmd {
        if (aggregateId == null || aggregateId.isBlank()) throw new IllegalArgumentException("aggregateId required");
        if (tellerId == null || tellerId.isBlank()) throw new IllegalArgumentException("tellerId required");
        if (terminalId == null || terminalId.isBlank()) throw new IllegalArgumentException("terminalId required");
    }
}

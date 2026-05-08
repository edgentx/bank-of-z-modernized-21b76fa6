package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new Teller Session.
 * Record carrying the necessary context for the operation.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    // Validation is performed by the Aggregate during execution
}

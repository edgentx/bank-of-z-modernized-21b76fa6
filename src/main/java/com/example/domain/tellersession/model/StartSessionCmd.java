package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Context: User-Interface-Navigation (Teller Terminal)
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
    // Validation logic is encapsulated within the Aggregate during execution
}

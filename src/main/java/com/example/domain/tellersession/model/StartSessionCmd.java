package com.example.domain.telllersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Validation of authentication and terminal state is enforced by the aggregate.
 */
public record StartSessionCmd(String aggregateId, String tellerId, String terminalId) implements Command {
}

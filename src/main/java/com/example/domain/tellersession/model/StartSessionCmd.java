package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Immutable record carrying necessary fields.
 */
public record StartSessionCmd(String aggregateId, String tellerId, String terminalId) implements Command {
}

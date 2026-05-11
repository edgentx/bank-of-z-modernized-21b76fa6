package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Command record carrying the request fields.
 */
public record StartSessionCmd(String aggregateId, String tellerId, String terminalId) implements Command {
}

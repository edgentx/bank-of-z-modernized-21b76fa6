package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Validated within the TellerSession aggregate.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
}

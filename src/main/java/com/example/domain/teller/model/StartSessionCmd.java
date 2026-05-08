package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Immutable record carrying the required data.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
}
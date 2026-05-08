package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Encapsulates the teller identity and the physical/virtual terminal being used.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {
}

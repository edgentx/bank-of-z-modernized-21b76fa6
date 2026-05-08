package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session.
 * Records the intent of a teller to start operations on a specific terminal.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}
package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a Teller Session.
 * Implements the shared Command marker interface.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}

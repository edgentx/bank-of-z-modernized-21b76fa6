package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a new teller session on a specific terminal.
 * Requires valid authentication context (implied by the aggregate state).
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}

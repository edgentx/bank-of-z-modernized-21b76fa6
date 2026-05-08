package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate a teller session.
 * Immutable record carrying the necessary request fields.
 */
public record StartSessionCmd(String sessionId, String tellerId, String terminalId) implements Command {}

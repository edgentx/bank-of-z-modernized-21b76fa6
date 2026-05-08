package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to end an existing teller session.
 * Records the intent to terminate the session.
 */
public record EndSessionCmd(String sessionId, String tellerId) implements Command {}

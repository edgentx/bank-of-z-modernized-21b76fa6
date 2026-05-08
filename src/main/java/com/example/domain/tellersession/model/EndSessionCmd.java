package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to end a Teller Session.
 * Validates session ID and potentially credentials to ensure authorized termination.
 */
public record EndSessionCmd(String sessionId, String tellerId) implements Command {}

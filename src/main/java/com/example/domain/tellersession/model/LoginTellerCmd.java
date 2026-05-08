package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Internal command to establish a valid authenticated session for testing.
 * Represents the "Login" action that must precede navigation.
 */
public record LoginTellerCmd(String sessionId, String tellerId) implements Command {}

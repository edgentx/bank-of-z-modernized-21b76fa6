package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to end a teller session.
 * Contains flags to simulate invalid states for BDD testing.
 */
public record EndSessionCmd(String sessionId, boolean isInCriticalState) implements Command {}

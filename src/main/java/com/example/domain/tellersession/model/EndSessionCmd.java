package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to end a teller session.
 * Terminates the teller session and clears sensitive session state.
 */
public record EndSessionCmd(String sessionId) implements Command {}

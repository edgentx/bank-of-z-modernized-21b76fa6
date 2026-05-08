package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active Teller Session.
 * Validates the sessionId and clears sensitive state.
 */
public record EndSessionCmd(String sessionId) implements Command {}

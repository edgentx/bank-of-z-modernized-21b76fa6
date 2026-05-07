package com.example.domain.tellermession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate the current teller session.
 * Clearing sensitive state and signaling the frontend to return to login.
 */
public record EndSessionCmd(String sessionId) implements Command {}

package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate the current Teller Session.
 * Clears sensitive state and invalidates the session ID.
 */
public record EndSessionCmd(String sessionId) implements Command {}

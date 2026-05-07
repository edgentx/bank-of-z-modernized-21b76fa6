package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active Teller Session.
 * Clears sensitive state and invalidates the session for further commands.
 */
public record EndSessionCmd(String sessionId) implements Command {}

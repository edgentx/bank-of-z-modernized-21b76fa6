package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active Teller Session.
 * Clears sensitive state and enforces invariants.
 */
public record EndSessionCmd(String sessionId) implements Command {}

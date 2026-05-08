package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active teller session.
 * Clears sensitive session state and invalidates the session identifier.
 */
public record EndSessionCmd(String sessionId) implements Command {}

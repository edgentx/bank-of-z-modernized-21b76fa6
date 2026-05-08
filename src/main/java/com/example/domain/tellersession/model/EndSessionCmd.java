package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active teller session.
 * Clears sensitive state and records the termination event.
 */
public record EndSessionCmd(String sessionId) implements Command {}

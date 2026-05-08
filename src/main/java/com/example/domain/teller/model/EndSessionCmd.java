package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active TellerSession.
 * Clears sensitive state and emits SessionEndedEvent.
 */
public record EndSessionCmd(String sessionId) implements Command {}

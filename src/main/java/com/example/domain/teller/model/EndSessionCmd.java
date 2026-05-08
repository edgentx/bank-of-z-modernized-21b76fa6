package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active TellerSession.
 * Validates sessionId presence.
 */
public record EndSessionCmd(String sessionId) implements Command {}

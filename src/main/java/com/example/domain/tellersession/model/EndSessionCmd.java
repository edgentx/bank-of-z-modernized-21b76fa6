package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active teller session.
 * Clears sensitive state from the TellerSession aggregate.
 */
public record EndSessionCmd(String sessionId) implements Command {}

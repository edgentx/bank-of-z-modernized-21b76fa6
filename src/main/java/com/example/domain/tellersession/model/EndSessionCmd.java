package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active TellerSession.
 * Clears sensitive state and signals UI navigation reset.
 */
public record EndSessionCmd(String sessionId) implements Command {}

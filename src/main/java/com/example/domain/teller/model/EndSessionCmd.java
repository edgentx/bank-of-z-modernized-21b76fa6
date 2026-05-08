package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to end a TellerSession.
 */
public record EndSessionCmd(String sessionId) implements Command {}

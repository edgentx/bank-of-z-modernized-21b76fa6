package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate a teller session.
 * S-20: Implement EndSessionCmd on TellerSession.
 */
public record EndSessionCmd(String sessionId) implements Command {}

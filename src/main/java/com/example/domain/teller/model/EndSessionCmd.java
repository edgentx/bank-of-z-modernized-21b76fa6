package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate the active teller session and clear sensitive state.
 * Part of S-20: TellerSession (user-interface-navigation).
 */
public record EndSessionCmd(String sessionId) implements Command {}

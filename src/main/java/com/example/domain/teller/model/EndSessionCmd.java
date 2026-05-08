package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate a Teller Session.
 * S-20: user-interface-navigation
 */
public record EndSessionCmd(String sessionId) implements Command {}

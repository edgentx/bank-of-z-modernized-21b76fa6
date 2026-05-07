package com.example.domain.tellermode.model;

import com.example.domain.shared.Command;

/**
 * Command to end a teller session.
 * Story: S-20
 */
public record EndSessionCmd(String sessionId) implements Command {}

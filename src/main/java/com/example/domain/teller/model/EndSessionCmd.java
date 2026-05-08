package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate a teller session.
 * Part of S-20: EndSessionCmd.
 */
public record EndSessionCmd(String sessionId) implements Command {}

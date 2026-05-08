package com.example.domain.tellerauth.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate a teller session.
 */
public record EndSessionCmd(String sessionId) implements Command {}

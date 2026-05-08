package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active teller session.
 * Corresponds to Story S-20.
 */
public record EndSessionCmd(String sessionId) implements Command {}

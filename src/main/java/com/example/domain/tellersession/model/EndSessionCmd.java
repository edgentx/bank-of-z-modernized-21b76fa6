package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active teller session.
 * Part of User-Interface-Navigation (S-20).
 */
public record EndSessionCmd(String sessionId) implements Command {}

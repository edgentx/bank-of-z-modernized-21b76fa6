package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active teller session.
 * ID: S-20
 */
public record EndSessionCmd(String sessionId) implements Command {}

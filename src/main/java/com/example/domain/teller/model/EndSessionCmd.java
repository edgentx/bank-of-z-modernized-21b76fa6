package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to end an active Teller Session.
 */
public record EndSessionCmd(String sessionId) implements Command {}

package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate a Teller Session.
 * ID: S-20
 */
public record EndSessionCmd(String sessionId) implements Command {}

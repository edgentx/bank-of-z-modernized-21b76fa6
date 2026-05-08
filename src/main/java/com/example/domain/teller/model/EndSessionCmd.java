package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active TellerSession.
 * Story: S-20.
 */
public record EndSessionCmd(String sessionId) implements Command {
    // Validation handled by aggregate or command validator; here we accept the raw data.
}

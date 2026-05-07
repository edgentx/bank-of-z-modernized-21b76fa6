package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active teller session.
 * ID: S-20
 */
public record EndSessionCmd(String sessionId, String reason) implements Command {
}

package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to end the current teller session.
 */
public record EndSessionCmd(String sessionId) implements Command {
}
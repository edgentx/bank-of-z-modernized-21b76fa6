package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to end an active teller session.
 */
public record EndSessionCmd(String sessionId) implements Command {
}
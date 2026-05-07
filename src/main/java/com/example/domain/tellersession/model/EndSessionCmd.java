package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate an active teller session.
 * Context: S-20 (TellerSession)
 */
public record EndSessionCmd(String sessionId) implements Command {
}

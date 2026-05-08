package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.Objects;

/**
 * Command to navigate the Teller Terminal UI to a specific menu/context.
 * Emulates 3270 screen flows (Enter, PF3, Clear, etc.)
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action, Instant timestamp) implements Command {

    public NavigateMenuCmd {
        // Defensive checks
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        // Allow empty string for menuId/action? Usually required, validated in Aggregate.
        if (timestamp == null) timestamp = Instant.now();
    }
}
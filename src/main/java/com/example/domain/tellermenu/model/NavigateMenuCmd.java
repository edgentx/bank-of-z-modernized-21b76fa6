package com.example.domain.tellermenu.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.UUID;

/**
 * Command to navigate the Teller UI to a specific menu context.
 * Corresponds to Story S-19.
 */
public record NavigateMenuCmd(
        String sessionId,
        String targetMenuId,
        String action, // e.g., Enter, Exit, PF3
        Instant timestamp
) implements Command {

    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("sessionId cannot be null or blank");
        }
        if (targetMenuId == null || targetMenuId.isBlank()) {
            throw new IllegalArgumentException("targetMenuId cannot be null or blank");
        }
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("action cannot be null or blank");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp cannot be null");
        }
    }

    // Convenience constructor using current time if not provided by caller
    public NavigateMenuCmd(String sessionId, String targetMenuId, String action) {
        this(sessionId, targetMenuId, action, Instant.now());
    }
}

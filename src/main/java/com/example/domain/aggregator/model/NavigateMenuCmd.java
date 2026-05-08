package com.example.domain.aggregator.model;

import com.example.domain.shared.Command;

import java.time.Instant;
import java.util.UUID;

/**
 * Command to route the teller to a different menu or screen.
 * (Story S-19)
 */
public record NavigateMenuCmd(
        String sessionId,
        String menuId,
        String action,
        Instant timestamp
) implements Command {
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId cannot be null");
        if (menuId == null || menuId.isBlank()) throw new IllegalArgumentException("menuId cannot be null");
        if (action == null || action.isBlank()) throw new IllegalArgumentException("action cannot be null");
    }
}
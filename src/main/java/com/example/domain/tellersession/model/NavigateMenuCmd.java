package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to route the teller to a different menu or screen.
 * Emulates legacy menu navigation (e.g. Enter, F3, PF keys).
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {
    // No-arg constructor for deserialization safety if needed, though record is fine here.
    public NavigateMenuCmd {
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("sessionId required");
    }
}

package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to route the teller to a different menu or screen.
 * Emulates legacy menu navigation (e.g., PF keys, Enter).
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {
    public NavigateMenuCmd {
        // Basic validation at construction if desired, though Aggregate handles domain logic.
        Objects.requireNonNull(sessionId, "sessionId required");
    }
}

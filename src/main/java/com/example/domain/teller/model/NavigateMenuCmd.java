package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to route the teller to a different menu or screen.
 * Used to emulate legacy 3270 menu navigation behavior.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {

    public NavigateMenuCmd {
        // Validation is handled by the aggregate, but we can enforce structural non-nulls here if desired.
        // For DDD, aggregates usually validate business rules, structural validation (like non-null)
        // can be here or in the aggregate. We will rely on the aggregate for full validation.
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(menuId, "menuId cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
    }
}

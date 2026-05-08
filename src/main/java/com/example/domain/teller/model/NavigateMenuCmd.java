package com.example.domain.teller.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to navigate the teller interface to a specific menu context.
 * S-19: Implement NavigateMenuCmd on TellerSession.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    public NavigateMenuCmd {
        // Validation moved to Aggregate, basic null checks here if desired
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
    }
}
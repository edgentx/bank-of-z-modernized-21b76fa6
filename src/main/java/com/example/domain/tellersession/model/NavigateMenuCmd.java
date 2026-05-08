package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to navigate the Teller Terminal UI to a specific menu or screen.
 * Emulates legacy 3270 data stream navigation.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action,
    String tellerId,
    boolean isAuthenticated,
    long lastActivityTimestampMillis,
    long timeoutMillis,
    String currentContextId // Represents the operational context ID the teller is currently on
) implements Command {

    public NavigateMenuCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(menuId, "menuId cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
    }
}

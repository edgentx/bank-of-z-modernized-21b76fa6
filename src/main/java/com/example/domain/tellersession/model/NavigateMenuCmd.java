package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to route the teller to a different menu or screen.
 * S-19: user-interface-navigation.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {

    public NavigateMenuCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(menuId, "menuId cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
    }
}

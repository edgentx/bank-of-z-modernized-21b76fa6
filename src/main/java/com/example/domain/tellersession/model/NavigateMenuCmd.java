package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to route the teller to a different menu or screen.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action
) implements Command {

    public NavigateMenuCmd {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        // MenuId and Action can be validated inside the aggregate logic
    }
}
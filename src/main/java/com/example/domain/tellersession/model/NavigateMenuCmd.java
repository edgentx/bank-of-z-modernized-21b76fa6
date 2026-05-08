package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to navigate the Teller UI from one screen to another.
 */
public record NavigateMenuCmd(
    String sessionId,
    String currentMenuId,
    String targetMenuId,
    String action
) implements Command {
    public NavigateMenuCmd {
        Objects.requireNonNull(sessionId, "sessionId required");
        Objects.requireNonNull(targetMenuId, "targetMenuId required");
        Objects.requireNonNull(action, "action required");
    }
}
package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to route the teller to a different menu or screen.
 * S-19: Implement NavigateMenuCmd.
 */
public record NavigateMenuCmd(String targetMenuId, String action) implements Command {
    public NavigateMenuCmd {
        Objects.requireNonNull(targetMenuId, "targetMenuId cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
        if (targetMenuId.isBlank()) throw new IllegalArgumentException("targetMenuId cannot be blank");
    }
}

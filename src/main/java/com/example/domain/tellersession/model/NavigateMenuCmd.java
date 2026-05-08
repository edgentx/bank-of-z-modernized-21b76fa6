package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to navigate the teller terminal interface to a new menu or screen.
 * S-19.
 */
public record NavigateMenuCmd(String targetMenuId, String action) implements Command {
    public NavigateMenuCmd {
        Objects.requireNonNull(targetMenuId);
        Objects.requireNonNull(action);
    }
}

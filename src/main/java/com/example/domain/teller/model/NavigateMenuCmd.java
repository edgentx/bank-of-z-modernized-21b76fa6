package com.example.domain.teller.model;

import com.example.domain.shared.Command;

import java.util.Objects;

/**
 * Command to navigate the teller terminal to a specific menu or screen.
 */
public record NavigateMenuCmd(String menuId, String action) implements Command {
    public NavigateMenuCmd {
        Objects.requireNonNull(menuId, "menuId cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
    }
}

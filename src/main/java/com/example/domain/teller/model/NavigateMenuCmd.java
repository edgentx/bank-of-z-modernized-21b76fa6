package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller terminal to a specific menu/screen.
 * Maps to S-19 domain requirement.
 */
public record NavigateMenuCmd(
        String sessionId,
        String menuId,
        String action
) implements Command {
}
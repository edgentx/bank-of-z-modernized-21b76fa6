package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller UI to a specific menu or screen.
 * Used to emulate legacy 3270 screen flows (e.g., F3=Back, Enter=Submit).
 */
public record NavigateMenuCmd(
        String sessionId,
        String menuId,
        String action
) implements Command {
}

package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller UI to a specific menu or screen.
 * Represents legacy 3270 AID keystrokes (PF3, Enter, etc.) mapped to new routes.
 */
public record NavigateMenuCmd(String menuId, String action) implements Command {
    // Validation logic is typically handled by the aggregate, but we can add basic checks here if needed.
}

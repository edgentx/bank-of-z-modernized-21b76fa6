package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller UI to a specific menu or screen.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
    // Validation of inputs can happen here if desired, or in the aggregate.
    // The aggregate is the ultimate authority.
}

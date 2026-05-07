package com.example.domain.tellermiddleware.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller Terminal UI to a specific menu or action.
 * Part of User Interface Navigation (S-19).
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {
    // Validation can be added here if necessary, though usually handled in the Aggregate.
}

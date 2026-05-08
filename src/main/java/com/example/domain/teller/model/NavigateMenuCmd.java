package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller terminal to a specific menu context.
 * Maps to the legacy 'Enter' or PF-key routing logic.
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {
    // Standard record for immutable command data
}
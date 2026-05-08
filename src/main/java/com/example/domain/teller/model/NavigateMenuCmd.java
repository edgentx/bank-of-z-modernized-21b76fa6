package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller UI to a specific menu context.
 * Part of S-19: TellerSession user-interface-navigation.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {
}
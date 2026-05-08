package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller UI to a specific menu or action.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}

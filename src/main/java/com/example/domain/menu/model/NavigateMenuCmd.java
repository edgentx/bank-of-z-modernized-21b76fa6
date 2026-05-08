package com.example.domain.menu.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller UI to a specific screen.
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {}

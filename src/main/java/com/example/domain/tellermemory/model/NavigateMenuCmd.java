package com.example.domain.tellermemory.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the Teller UI to a specific menu or screen.
 * Part of the legacy emulation layer for 3270/terminal workflows.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}

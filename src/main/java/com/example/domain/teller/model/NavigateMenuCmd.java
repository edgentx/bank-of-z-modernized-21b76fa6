package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller interface to a specific menu/screen.
 * Used to emulate legacy 3270 navigation flows.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action
) implements Command {}

package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller interface to a specific menu context.
 * S-19: Implement NavigateMenuCmd on TellerSession.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}

package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller interface to a specific menu.
 * Part of user-interface-navigation (S-19).
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {}

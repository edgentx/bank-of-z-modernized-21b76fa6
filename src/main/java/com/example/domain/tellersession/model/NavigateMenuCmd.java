package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller interface to a specific menu or screen.
 * Part of S-19: TellerSession UI Navigation.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}

package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller interface to a specific menu or action.
 * Part of S-19: User Interface Navigation.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}

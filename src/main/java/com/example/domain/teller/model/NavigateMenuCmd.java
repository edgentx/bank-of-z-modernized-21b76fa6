package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller interface to a specific menu.
 * Part of S-19: User Interface Navigation.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action,
    String tellerId,
    String currentContextId
) implements Command {}

package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller interface to a specific menu.
 */
public record NavigateMenuCmd(
    String sessionId,
    String targetMenuId,
    String action // e.g. 'ENTER', 'PF3', 'TAB'
) implements Command {}

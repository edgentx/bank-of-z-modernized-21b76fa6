package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command for a Teller to navigate to a specific Menu/Screen.
 * S-19: user-interface-navigation.
 */
public record NavigateMenuCmd(
    String sessionId,
    String menuId,
    String action
) implements Command {}
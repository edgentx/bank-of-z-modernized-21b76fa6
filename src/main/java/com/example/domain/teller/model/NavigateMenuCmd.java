package com.example.domain.teller.model;

import com.example.domain.shared.Command;

/**
 * Command for a Teller to navigate to a specific screen/menu.
 * Part of the S-19 User Interface Navigation story.
 */
public record NavigateMenuCmd(String sessionId, String menuId, String action) implements Command {}

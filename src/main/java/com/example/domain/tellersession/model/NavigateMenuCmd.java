package com.example.domain.tellersession.model;

import com.example.domain.shared.Command;

/**
 * Command to navigate the teller terminal to a specific screen or menu.
 */
public record NavigateMenuCmd(String sessionId, String targetMenuId, String action) implements Command {}

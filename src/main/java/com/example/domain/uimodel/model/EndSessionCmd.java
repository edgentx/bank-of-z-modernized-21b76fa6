package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to terminate a Teller Session.
 * Part of Story S-20: EndSessionCmd.
 */
public record EndSessionCmd(String sessionId) implements Command {}

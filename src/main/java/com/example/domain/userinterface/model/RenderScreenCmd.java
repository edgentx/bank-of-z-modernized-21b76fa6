package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

/**
 * Command to request a screen rendering.
 */
public record RenderScreenCmd(String aggregateId, String screenId, String deviceType) implements Command {}

package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to generate the presentation layout for a specific screen.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {}

package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen.
 * Story: S-21 Implement RenderScreenCmd.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {}

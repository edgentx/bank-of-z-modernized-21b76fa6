package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

/**
 * Command to render a screen layout adapted for a specific device.
 */
public record RenderScreenCmd(String screenId, String deviceType, String layoutName) implements Command {}

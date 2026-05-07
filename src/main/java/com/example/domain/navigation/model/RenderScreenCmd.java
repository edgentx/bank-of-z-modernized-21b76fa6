package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout for a device.
 * Part of User Interface Navigation (S-21).
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {}

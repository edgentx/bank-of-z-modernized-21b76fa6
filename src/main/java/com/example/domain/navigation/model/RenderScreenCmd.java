package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen map adapted for a user's device.
 * Story S-21: RenderScreenCmd on ScreenMap.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {}

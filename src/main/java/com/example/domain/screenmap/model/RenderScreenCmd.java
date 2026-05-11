package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

/**
 * Command to request a screen rendering layout.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {}

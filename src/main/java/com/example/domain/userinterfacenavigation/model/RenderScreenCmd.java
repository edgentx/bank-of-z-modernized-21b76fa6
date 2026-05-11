package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to trigger the rendering of a specific screen layout adapted for a device.
 */
public record RenderScreenCmd(
    String screenId,
    String deviceType,
    boolean mandatoryFieldsValid,
    boolean fieldLengthsValid
) implements Command {}

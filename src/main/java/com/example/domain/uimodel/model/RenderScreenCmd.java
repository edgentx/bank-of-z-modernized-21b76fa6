package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout.
 * S-21: User Interface Navigation
 */
public record RenderScreenCmd(
    String aggregateId,
    String screenId,
    String deviceType
) implements Command {
}
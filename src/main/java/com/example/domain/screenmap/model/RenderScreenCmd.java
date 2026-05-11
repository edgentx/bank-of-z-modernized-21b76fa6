package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to render a specific screen layout.
 * Story: S-21
 */
public record RenderScreenCmd(
    String aggregateId,
    String screenId,
    String deviceType,
    Map<String, String> inputData
) implements Command {}

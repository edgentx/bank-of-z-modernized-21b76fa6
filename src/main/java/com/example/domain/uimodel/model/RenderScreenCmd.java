package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to render a specific screen layout.
 * Story S-21: Implement RenderScreenCmd on ScreenMap.
 */
public record RenderScreenCmd(String screenId, String deviceType, Map<String, Object> context) implements Command {
}
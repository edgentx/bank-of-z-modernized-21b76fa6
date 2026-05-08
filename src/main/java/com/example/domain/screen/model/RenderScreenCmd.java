package com.example.domain.screen.model;

import com.example.domain.shared.Command;
import java.util.Map;

public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    String deviceType,
    Map<String, String> inputFields
) implements Command {}

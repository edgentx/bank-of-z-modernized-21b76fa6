package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;
import java.util.List;

public record RenderScreenCmd(
    String screenId,
    String deviceType,
    List<String> fields
) implements Command {}

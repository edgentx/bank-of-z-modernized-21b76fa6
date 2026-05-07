package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

public record RenderScreenCmd(String screenMapId, String screenId, String deviceType) implements Command {}

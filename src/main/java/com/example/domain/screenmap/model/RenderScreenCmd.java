package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

public record RenderScreenCmd(String screenMapId, String screenId, String deviceType) implements Command {
  public RenderScreenCmd {
    if (screenMapId == null || screenMapId.isBlank()) {
      throw new IllegalArgumentException("ScreenMap ID cannot be null or blank");
    }
    if (screenId == null || screenId.isBlank()) {
      throw new IllegalArgumentException("Screen ID cannot be null or blank");
    }
    if (deviceType == null || deviceType.isBlank()) {
      throw new IllegalArgumentException("Device type cannot be null or blank");
    }
  }
}

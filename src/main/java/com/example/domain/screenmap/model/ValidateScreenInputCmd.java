package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record ValidateScreenInputCmd(String screenMapId, String screenId, Map<String, String> inputFields) implements Command {
  public ValidateScreenInputCmd {
    if (screenMapId == null || screenMapId.isBlank()) {
      throw new IllegalArgumentException("ScreenMap ID cannot be null or blank");
    }
    if (screenId == null || screenId.isBlank()) {
      throw new IllegalArgumentException("Screen ID cannot be null or blank");
    }
    if (inputFields == null || inputFields.isEmpty()) {
      throw new IllegalArgumentException("Input fields cannot be null or empty");
    }
    inputFields = Collections.unmodifiableMap(new LinkedHashMap<>(inputFields));
  }
}

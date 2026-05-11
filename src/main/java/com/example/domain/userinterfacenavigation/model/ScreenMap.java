package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class ScreenMap extends AggregateRoot {
  private final String screenId;
  private Map<String, FieldDefinition> fieldDefinitions = new HashMap<>();

  public ScreenMap(String screenId) {
    this.screenId = screenId;
    // Initialize with some default definitions for testing purposes
    // In a real scenario, these would be loaded via events or a constructor
    fieldDefinitions.put("ACCOUNT", new FieldDefinition("ACCOUNT", 10, true));
    fieldDefinitions.put("AMOUNT", new FieldDefinition("AMOUNT", 12, true));
  }

  @Override public String id() { return screenId; }

  @Override public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof ValidateScreenInputCmd c) {
      return validateInput(c);
    }
    throw new UnknownCommandException(cmd);
  }

  private List<DomainEvent> validateInput(ValidateScreenInputCmd cmd) {
    if (cmd.inputFields() == null) {
      throw new IllegalArgumentException("Input fields cannot be null");
    }

    // 1. Validate Mandatory Fields
    for (Map.Entry<String, FieldDefinition> entry : fieldDefinitions.entrySet()) {
      if (entry.getValue().mandatory()) {
        if (!cmd.inputFields().containsKey(entry.getKey()) || 
            cmd.inputFields().get(entry.getKey()) == null || 
            cmd.inputFields().get(entry.getKey()).isBlank()) {
          throw new IllegalStateException("All mandatory input fields must be validated before screen submission. Missing: " + entry.getKey());
        }
      }
    }

    // 2. Validate Field Lengths (BMS Constraints)
    for (Map.Entry<String, String> entry : cmd.inputFields().entrySet()) {
      FieldDefinition def = fieldDefinitions.get(entry.getKey());
      if (def != null) {
        if (entry.getValue().length() > def.length()) {
          throw new IllegalStateException("Field lengths must strictly adhere to legacy BMS constraints during the transition period. Violation: " + entry.getKey());
        }
      }
    }

    // Success
    var event = new ScreenInputValidatedEvent(screenId, cmd.screenId(), cmd.inputFields(), Instant.now());
    addEvent(event);
    incrementVersion();
    return List.of(event);
  }

  public void addFieldDefinition(String name, int length, boolean mandatory) {
    fieldDefinitions.put(name, new FieldDefinition(name, length, mandatory));
  }

  private record FieldDefinition(String name, int length, boolean mandatory) {}
}

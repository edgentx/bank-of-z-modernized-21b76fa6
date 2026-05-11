package com.example.domain.screen.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ScreenMap aggregate.
 * Manages the state and validation rules for 3270/BMS screen navigation.
 */
public class ScreenMap extends AggregateRoot {

  private final String aggregateId;
  private String screenId;
  // Invariant: Field definitions for this screen (fieldName -> maxLength)
  private final Map<String, Integer> fieldDefinitions = new HashMap<>();
  // Invariant: Mandatory fields
  private final List<String> mandatoryFields = new ArrayList<>();

  public ScreenMap(String aggregateId) {
    this.aggregateId = aggregateId;
    // Initialize with default BMS constraints for test purposes
    // (In a real scenario, this would be loaded via an event or repo)
    this.screenId = "DEFAULT_SCREEN";
    this.fieldDefinitions.put("ACC_NUM", 10);
    this.fieldDefinitions.put("TX_AMT", 12);
    this.mandatoryFields.add("ACC_NUM");
  }

  // Used by test to simulate a loaded aggregate with specific rules
  public void configureField(String fieldName, int maxLength, boolean isMandatory) {
    this.fieldDefinitions.put(fieldName, maxLength);
    if (isMandatory) {
      if (!this.mandatoryFields.contains(fieldName)) {
        this.mandatoryFields.add(fieldName);
      }
    } else {
      this.mandatoryFields.remove(fieldName);
    }
  }

  @Override
  public String id() {
    return aggregateId;
  }

  @Override
  public List<DomainEvent> execute(Command cmd) {
    if (cmd instanceof ValidateScreenInputCmd c) {
      return handleValidateInput(c);
    }
    throw new UnknownCommandException(cmd);
  }

  private List<DomainEvent> handleValidateInput(ValidateScreenInputCmd cmd) {
    // 1. Validate Mandatory Fields
    for (String field : mandatoryFields) {
      String value = cmd.inputFields().get(field);
      if (value == null || value.trim().isEmpty()) {
        throw new IllegalArgumentException("All mandatory input fields must be validated before screen submission. Missing: " + field);
      }
    }

    // 2. Validate Field Lengths (BMS Constraints)
    for (Map.Entry<String, String> entry : cmd.inputFields().entrySet()) {
      String fieldName = entry.getKey();
      String value = entry.getValue();

      // If the field is known in the map, check length.
      // We generally validate fields provided in input against known definitions.
      if (fieldDefinitions.containsKey(fieldName)) {
        int maxLength = fieldDefinitions.get(fieldName);
        if (value != null && value.length() > maxLength) {
          throw new IllegalArgumentException(
            String.format("Field lengths must strictly adhere to legacy BMS constraints during the transition period. Field '%s' max %d, got %d",
              fieldName, maxLength, value.length())
          );
        }
      }
    }

    var event = new ScreenInputValidatedEvent(this.aggregateId, cmd.screenId(), Instant.now());
    addEvent(event);
    incrementVersion();
    return List.of(event);
  }
}
package com.example.domain.screenmap.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record InputValidatedEvent(
  String type,
  String aggregateId,
  Instant occurredAt,
  String screenId,
  Map<String, String> inputFields
) implements DomainEvent {
  public InputValidatedEvent {
    if (type == null) type = "input.validated";
    if (inputFields != null) {
      inputFields = Collections.unmodifiableMap(new LinkedHashMap<>(inputFields));
    }
  }

  public static InputValidatedEvent create(String aggregateId, String screenId, Map<String, String> inputFields) {
    return new InputValidatedEvent("input.validated", aggregateId, Instant.now(), screenId, inputFields);
  }
}

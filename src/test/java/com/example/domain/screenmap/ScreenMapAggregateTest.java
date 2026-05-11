package com.example.domain.screenmap;

import com.example.domain.screenmap.model.InputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for ScreenMapAggregate covering Story S-22.
 * Uses standard JUnit 5 patterns; mocks are not required as this is a pure domain object test.
 */
class ScreenMapAggregateTest {

  // Scenario: Successfully execute ValidateScreenInputCmd
  @Test
  void testExecuteValidateScreenInputCmd_Success() {
    // Given a valid ScreenMap aggregate
    var aggregate = new ScreenMapAggregate("screen-1");
    var validFields = Map.of("ACCOUNT_NUMBER", "12345", "AMOUNT", "100.00");
    var cmd = new ValidateScreenInputCmd("TX-100", validFields);

    // When the ValidateScreenInputCmd command is executed
    var events = aggregate.execute(cmd);

    // Then a input.validated event is emitted
    assertEquals(1, events.size());
    assertTrue(events.get(0) instanceof InputValidatedEvent);
    
    var event = (InputValidatedEvent) events.get(0);
    assertEquals("screen-1", event.aggregateId());
    assertEquals("TX-100", event.screenId());
    assertEquals(2, event.inputFields().size());
    assertTrue(event.occurredAt().isBefore(Instant.now().plusSeconds(1)));
  }

  // Scenario: ValidateScreenInputCmd rejected — All mandatory input fields must be validated before screen submission.
  @Test
  void testExecuteValidateScreenInputCmd_Rejected_MissingMandatoryField() {
    // Given a ScreenMap aggregate that violates: All mandatory input fields must be validated
    var aggregate = new ScreenMapAggregate("screen-1");
    var invalidFields = Map.of("AMOUNT", "100.00"); // Missing ACCOUNT_NUMBER
    var cmd = new ValidateScreenInputCmd("TX-100", invalidFields);

    // When the ValidateScreenInputCmd command is executed
    // Then the command is rejected with a domain error
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      aggregate.execute(cmd);
    });

    assertTrue(exception.getMessage().contains("Mandatory field"));
  }

  // Scenario: ValidateScreenInputCmd rejected — Field lengths must strictly adhere to legacy BMS constraints.
  @Test
  void testExecuteValidateScreenInputCmd_Rejected_LengthConstraint() {
    // Given a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints.
    var aggregate = new ScreenMapAggregate("screen-1");
    // Assuming max length 10 for this legacy field
    var invalidFields = Map.of("ACCOUNT_NUMBER", "1234567890123"); // Length 13
    var cmd = new ValidateScreenInputCmd("TX-100", invalidFields);

    // When the ValidateScreenInputCmd command is executed
    // Then the command is rejected with a domain error
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      aggregate.execute(cmd);
    });

    assertTrue(exception.getMessage().contains("exceeds maximum length"));
  }
}

package com.example.domain.screening;

import com.example.domain.screening.model.ScreenInputValidatedEvent;
import com.example.domain.screening.model.ScreenMap;
import com.example.domain.screening.model.ValidateScreenInputCmd;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for S-22: ValidateScreenInputCmd.
 * Tests cover validation rules for mandatory fields and BMS field length constraints.
 */
class ScreenMapTest {

    private static final String SCREEN_ID = "LOGIN_SCREEN";

    @Test
    void testExecuteValidateScreenInputCmd_Success() {
        // Given
        ScreenMap aggregate = new ScreenMap(SCREEN_ID);
        // Input contains valid data for mandatory fields
        Map<String, String> validInput = Map.of(
            "USER_ID", "ALICE",
            "PASSWORD", "SECRET123"
        );
        ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(SCREEN_ID, validInput);

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertNotNull(events);
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof ScreenInputValidatedEvent);
        
        ScreenInputValidatedEvent event = (ScreenInputValidatedEvent) events.get(0);
        assertEquals("input.validated", event.type());
        assertEquals(SCREEN_ID, event.aggregateId());
        assertEquals(validInput, event.inputFields());
    }

    @Test
    void testExecuteValidateScreenInputCmd_Rejected_MissingMandatoryFields() {
        // Given
        ScreenMap aggregate = new ScreenMap(SCREEN_ID);
        // Input is missing mandatory 'PASSWORD' field
        Map<String, String> invalidInput = Map.of("USER_ID", "BOB");
        ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(SCREEN_ID, invalidInput);

        // When & Then
        // Expected behavior: Domain error is thrown or command rejected
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("mandatory"));
    }

    @Test
    void testExecuteValidateScreenInputCmd_Rejected_FieldLengthExceedsBMSConstraints() {
        // Given
        ScreenMap aggregate = new ScreenMap(SCREEN_ID);
        // Legacy BMS map defines USER_ID as max 8 chars. Input is 10 chars.
        Map<String, String> oversizedInput = Map.of(
            "USER_ID", "CHARLIE_BRavo", // > 8 chars
            "PASSWORD", "PWD"
        );
        ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(SCREEN_ID, oversizedInput);

        // When & Then
        // Expected behavior: Domain error regarding BMS length constraints
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(exception.getMessage().contains("length") || exception.getMessage().contains("BMS"));
    }
}

package com.example.domain.uimodel;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.ScreenInputValidatedEvent;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ValidateScreenInputCmd;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TDD Red Phase tests for ScreenMapAggregate (S-22).
 * Verifies validation rules for legacy 3270 screen input.
 */
class ScreenMapAggregateTest {

    private static final String SCREEN_ID = "LOGIN_SCREEN_01";

    @Test
    void shouldEmitInputValidatedEventWhenInputIsValid() {
        // Given
        var aggregate = new ScreenMapAggregate(SCREEN_ID);
        var validInput = Map.of("ACCT_NO", "123456789", "PIN", "1234");
        var cmd = new ValidateScreenInputCmd(SCREEN_ID, validInput);

        // When
        var events = aggregate.execute(cmd);

        // Then
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(ScreenInputValidatedEvent.class);
        
        var event = (ScreenInputValidatedEvent) events.get(0);
        assertThat(event.type()).isEqualTo("input.validated");
        assertThat(event.aggregateId()).isEqualTo(SCREEN_ID);
        assertThat(event.inputFields()).containsKey("ACCT_NO");
        assertThat(event.occurredAt()).isBefore(Instant.now());
    }

    @Test
    void shouldRejectCommandWhenMandatoryFieldIsMissing() {
        // Given a ScreenMap aggregate that violates: All mandatory input fields must be validated
        var aggregate = new ScreenMapAggregate(SCREEN_ID);
        var invalidInput = Map.of("PIN", "1234"); // Missing ACCT_NO
        var cmd = new ValidateScreenInputCmd(SCREEN_ID, invalidInput);

        // When & Then
        assertThatThrownBy(() -> aggregate.execute(cmd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Mandatory field")
                .hasMessageContaining("ACCT_NO")
                .hasMessageContaining("missing or empty");
    }

    @Test
    void shouldRejectCommandWhenMandatoryFieldIsEmpty() {
        // Given
        var aggregate = new ScreenMapAggregate(SCREEN_ID);
        var invalidInput = Map.of("ACCT_NO", "   ");
        var cmd = new ValidateScreenInputCmd(SCREEN_ID, invalidInput);

        // When & Then
        assertThatThrownBy(() -> aggregate.execute(cmd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("missing or empty");
    }

    @Test
    void shouldRejectCommandWhenFieldLengthExceedsBMSConstraints() {
        // Given a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints
        var aggregate = new ScreenMapAggregate(SCREEN_ID);
        // BMS length for ACCT_NO is 12
        var invalidInput = Map.of("ACCT_NO", "1234567890123"); // Length 13
        var cmd = new ValidateScreenInputCmd(SCREEN_ID, invalidInput);

        // When & Then
        assertThatThrownBy(() -> aggregate.execute(cmd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exceeds maximum BMS length")
                .hasMessageContaining("12");
    }

    @Test
    void shouldThrowUnknownCommandForUnsupportedCommands() {
        // Given
        var aggregate = new ScreenMapAggregate(SCREEN_ID);
        var unknownCmd = new Object(); // Not a valid command

        // When & Then
        // We cast to Command to satisfy API, though logic checks type
        assertThatThrownBy(() -> aggregate.execute((com.example.domain.shared.Command) unknownCmd))
                .isInstanceOf(UnknownCommandException.class);
    }
}
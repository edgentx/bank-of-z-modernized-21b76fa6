package com.example.domain.screenmap;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for ScreenMapAggregate (Story S-21).
 * Verifies TDD Red/Green/Refactor cycles for RenderScreenCmd.
 */
class ScreenMapAggregateTest {

    // Scenario: Successfully execute RenderScreenCmd
    @Test
    void shouldEmitScreenRenderedEventWhenCommandIsValid() {
        // Given
        String aggregateId = "map-01";
        String screenId = "LOGIN_SCREEN";
        String deviceType = "3270_TERMINAL";
        String layoutContent = "{\"fields\": [{\"id\":\"user\",\"length\":10}]}"; // Valid JSON, valid length
        RenderScreenCmd cmd = new RenderScreenCmd(screenId, deviceType, layoutContent);
        ScreenMapAggregate aggregate = new ScreenMapAggregate(aggregateId);

        // When
        List events = aggregate.execute(cmd);

        // Then
        assertNotNull(events);
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) events.get(0);
        assertEquals(aggregateId, event.aggregateId());
        assertEquals(screenId, event.screenId());
        assertEquals(deviceType, event.deviceType());
        assertEquals("screen.rendered", event.type());
        assertNotNull(event.occurredAt());
        
        // Verify aggregate state mutation (uncommitted events)
        assertEquals(1, aggregate.uncommittedEvents().size());
    }

    // Scenario: RenderScreenCmd rejected — All mandatory input fields must be validated
    @Test
    void shouldRejectCommandIfScreenIdIsMissing() {
        // Given
        ScreenMapAggregate aggregate = new ScreenMapAggregate("map-01");
        RenderScreenCmd cmd = new RenderScreenCmd(null, "MOBILE", "{}");

        // When/Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("screenId is mandatory"));
    }

    @Test
    void shouldRejectCommandIfScreenIdIsBlank() {
        // Given
        ScreenMapAggregate aggregate = new ScreenMapAggregate("map-01");
        RenderScreenCmd cmd = new RenderScreenCmd("   ", "MOBILE", "{}");

        // When/Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("screenId is mandatory"));
    }

    @Test
    void shouldRejectCommandIfDeviceTypeIsMissing() {
        // Given
        ScreenMapAggregate aggregate = new ScreenMapAggregate("map-01");
        RenderScreenCmd cmd = new RenderScreenCmd("LOGIN", null, "{}");

        // When/Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("deviceType is mandatory"));
    }

    @Test
    void shouldRejectCommandIfLayoutContentIsMissing() {
        // Given
        ScreenMapAggregate aggregate = new ScreenMapAggregate("map-01");
        RenderScreenCmd cmd = new RenderScreenCmd("LOGIN", "MOBILE", null);

        // When/Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("layoutContent is mandatory"));
    }

    // Scenario: RenderScreenCmd rejected — Field lengths must strictly adhere to legacy BMS constraints
    @Test
    void shouldRejectCommandIfLayoutExceedsLegacyBMSLength() {
        // Given
        ScreenMapAggregate aggregate = new ScreenMapAggregate("map-01");
        String longContent = "a".repeat(1025); // Exceeds 1024 limit
        RenderScreenCmd cmd = new RenderScreenCmd("LOGIN", "MOBILE", longContent);

        // When/Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("Field length exceeds legacy BMS constraints"));
    }

    @Test
    void shouldAcceptCommandIfLayoutMatchesLegacyBMSLimit() {
        // Given
        ScreenMapAggregate aggregate = new ScreenMapAggregate("map-01");
        String exactContent = "a".repeat(1024); // Exactly 1024
        RenderScreenCmd cmd = new RenderScreenCmd("LOGIN", "MOBILE", exactContent);

        // When
        List events = aggregate.execute(cmd);

        // Then
        assertFalse(events.isEmpty());
        assertEquals(1, aggregate.uncommittedEvents().size());
    }

    @Test
    void shouldThrowUnknownCommandExceptionForUnsupportedCommand() {
        // Given
        ScreenMapAggregate aggregate = new ScreenMapAggregate("map-01");
        Command unsupportedCmd = new Command() {}; // Anonymous command

        // When/Then
        assertThrows(com.example.domain.shared.UnknownCommandException.class, () -> {
            aggregate.execute(unsupportedCmd);
        });
    }
}

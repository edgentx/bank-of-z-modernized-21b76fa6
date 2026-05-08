package com.example.adapters;

import com.example.ports.SlackNotifier;
import com.example.mocks.InMemoryEventStore;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RealSlackNotifierTest {

    @Test
    public void testSendStoresMessageInMemory() {
        // Given
        InMemoryEventStore store = new InMemoryEventStore();
        SlackNotifier notifier = new RealSlackNotifier(store);
        String message = "Alert: VW-454 is fixed.";

        // When
        notifier.send(message);

        // Then
        assertEquals(1, store.getEvents().size());
        assertTrue(store.getEvents().get(0).contains(message));
    }

    @Test
    public void testSendValidatesInput() {
        // Given
        InMemoryEventStore store = new InMemoryEventStore();
        SlackNotifier notifier = new RealSlackNotifier(store);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            notifier.send(null);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            notifier.send("");
        });
    }
}

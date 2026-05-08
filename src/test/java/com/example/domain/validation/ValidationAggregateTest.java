package com.example.domain.validation;

import com.example.domain.validation.model.*;
import com.example.domain.shared.UnknownCommandException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationAggregateTest {

    @Test
    void shouldVerifyLinkWhenUrlExistsInBody() {
        // Arrange
        ValidationAggregate aggregate = new ValidationAggregate("test-id");
        String body = "Check this out: https://github.com/example/repo/issues/123";
        String url = "https://github.com/example/repo/issues/123";
        VerifySlackLinkCmd cmd = new VerifySlackLinkCmd("test-id", body, url);

        // Act
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size());
        assertTrue(aggregate.isLinkVerified());
        assertTrue(((SlackLinkVerifiedEvent) events.get(0)).found());
    }

    @Test
    void shouldFailVerificationWhenUrlMissingFromBody() {
        // Arrange
        ValidationAggregate aggregate = new ValidationAggregate("test-id");
        String body = "Link is missing here.";
        String url = "https://github.com/example/repo/issues/123";
        VerifySlackLinkCmd cmd = new VerifySlackLinkCmd("test-id", body, url);

        // Act
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Assert
        assertEquals(1, events.size());
        assertFalse(aggregate.isLinkVerified());
        assertFalse(((SlackLinkVerifiedEvent) events.get(0)).found());
    }

    @Test
    void shouldThrowExceptionForUnknownCommand() {
        // Arrange
        ValidationAggregate aggregate = new ValidationAggregate("test-id");
        
        // Act & Assert
        assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute(new Command() {});
        });
    }
}

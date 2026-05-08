package com.example.domain.notification;

import com.example.domain.notification.model.NotificationAggregate;
import com.example.domain.notification.model.PrepareSlackNotificationCmd;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NotificationAggregate.
 * Verifies domain logic regarding content preparation.
 */
public class NotificationAggregateTest {

    @Test
    @DisplayName("Should prepare notification body containing GitHub URL")
    public void testPrepareNotificationWithUrl() {
        // Arrange
        String id = "notif-1";
        String url = "https://github.com/test/123";
        NotificationAggregate aggregate = new NotificationAggregate(id);
        PrepareSlackNotificationCmd cmd = new PrepareSlackNotificationCmd(
            id, 
            "#general", 
            "Test Defect", 
            url
        );

        // Act
        aggregate.execute(cmd);

        // Assert
        assertTrue(aggregate.getSlackBody().contains(url), "Slack body must contain the GitHub URL");
    }

    @Test
    @DisplayName("Should throw exception if GitHub URL is missing")
    public void testPrepareNotificationFailsWithoutUrl() {
        // Arrange
        String id = "notif-2";
        NotificationAggregate aggregate = new NotificationAggregate(id);
        PrepareSlackNotificationCmd cmd = new PrepareSlackNotificationCmd(
            id, 
            "#general", 
            "Test Defect", 
            "" // Empty URL
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
    }
}
package com.example.domain.notification;

import com.example.domain.notification.model.DefectReportedEvent;
import com.example.domain.notification.model.NotificationAggregate;
import com.example.domain.notification.model.ReportDefectCmd;
import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationAggregateTest {

    @Test
    void shouldFormatBodyWithGitHubUrlWhenUrlIsValid() {
        // Given
        String id = "notif-1";
        String githubUrl = "https://github.com/bank-of-z/issues/issues/454";
        NotificationAggregate aggregate = new NotificationAggregate(id);
        ReportDefectCmd cmd = new ReportDefectCmd(id, "VW-454", "URL missing in body", githubUrl);

        // When
        List<DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertEquals(1, events.size());
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        
        assertNotNull(event);
        assertEquals(id, event.aggregateId());
        assertEquals("Defect Reported: VW-454\nGitHub Issue: <https://github.com/bank-of-z/issues/issues/454>", event.formattedBody());
        assertTrue(event.formattedBody().contains("<" + githubUrl + ">"));
        assertTrue(event.formattedBody().contains("GitHub Issue:"));
    }

    @Test
    void shouldThrowExceptionWhenGitHubUrlIsInvalid() {
        // Given
        String id = "notif-2";
        String invalidUrl = "https://gitlab.com/bank-of-z/issues/123";
        NotificationAggregate aggregate = new NotificationAggregate(id);
        ReportDefectCmd cmd = new ReportDefectCmd(id, "S-123", "External link", invalidUrl);

        // When/Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("valid GitHub Issue URL"));
    }

    @Test
    void shouldThrowExceptionWhenGitHubUrlIsNull() {
        // Given
        String id = "notif-3";
        NotificationAggregate aggregate = new NotificationAggregate(id);
        ReportDefectCmd cmd = new ReportDefectCmd(id, "S-999", "No link provided", null);

        // When/Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("valid GitHub Issue URL"));
    }

    @Test
    void shouldThrowExceptionWhenTitleIsBlank() {
        // Given
        String id = "notif-4";
        NotificationAggregate aggregate = new NotificationAggregate(id);
        ReportDefectCmd cmd = new ReportDefectCmd(id, "", "No title", "https://github.com/bank-of/z/issues/1");

        // When/Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> aggregate.execute(cmd));
        assertTrue(ex.getMessage().contains("title"));
    }
}

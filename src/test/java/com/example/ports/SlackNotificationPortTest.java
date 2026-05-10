package com.example.ports;

import com.example.domain.validation.model.DefectReportedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.*;

/**
 * TDD Red Phase: Verifying that the application layer correctly uses
 * the Slack adapter to post the defect report.
 */
@ExtendWith(MockitoExtension.class)
class SlackNotificationPortTest {

    @Mock
    private SlackNotificationPort slackNotificationPort;

    @Test
    void shouldPostMessageContainingGitHubUrl() {
        // Given
        DefectReportedEvent event = new DefectReportedEvent(
            "VW-454",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
            "Validating VW-454",
            Instant.now()
        );
        
        String expectedBodySnippet = "https://github.com/bank-of-z/vforce360/issues/454";

        // When
        // In a real handler, we would invoke: slackNotificationPort.notify(event);
        // For this red-phase test, we simulate the handler logic here.
        slackNotificationPort.sendNotification(event.getSlackBody());

        // Then
        verify(slackNotificationPort, times(1)).sendNotification(argThat(body -> 
            body != null && body.contains(expectedBodySnippet)
        ));
    }
}
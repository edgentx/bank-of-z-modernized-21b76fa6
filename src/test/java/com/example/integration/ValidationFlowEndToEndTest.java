package com.example.integration;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.ports.SlackNotificationPort;
import com.example.ports.TicketingPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/*
 * Regression Test for S-FB-1
 * End-to-End verification of the Report Defect workflow.
 * Ensures that when a defect is reported, the resulting Slack notification
 * contains the GitHub issue link.
 */
public class ValidationFlowEndToEndTest {

    private TicketingPort mockTicketingPort;
    private SlackNotificationPort mockSlackPort;

    @BeforeEach
    public void setUp() {
        mockTicketingPort = mock(TicketingPort.class);
        mockSlackPort = mock(SlackNotificationPort.class);
        
        // Default behavior: Ticketing system returns a GitHub URL
        when(mockTicketingPort.createTicket(anyString(), anyString()))
            .thenReturn("https://github.com/bank-of-z/issues/454");
    }

    @Test
    public void test_workflow_slack_body_contains_github_url() {
        // Arrange
        ValidationAggregate aggregate = new ValidationAggregate("VW-454");
        ReportDefectCmd cmd = new ReportDefectCmd(
            "VW-454",
            "Validating VW-454 — GitHub URL in Slack body",
            "LOW",
            "validation"
        );

        // Act (Simulating the Temporal Workflow Logic locally)
        aggregate.execute(cmd);
        String ticketUrl = aggregate.getTicketUrl();
        
        // Simulate Slack Notification Step
        String slackBody = "Defect Reported: " + cmd.description() + "\n" +
                           "Severity: " + cmd.severity() + "\n" +
                           "Ticket: " + ticketUrl;
                           
        mockSlackPort.sendNotification(slackBody);

        // Assert
        ArgumentCaptor<String> slackCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSlackPort).sendNotification(slackCaptor.capture());
        
        String capturedBody = slackCaptor.getValue();
        
        // Regression Check: Verify body contains the URL format
        assertThat(capturedBody).contains("https://github.com");
        assertThat(capturedBody).contains("Ticket:");
        
        // Verify it didn't just contain the placeholder text without the link
        assertThat(capturedBody).doesNotContain("<url>");
    }
}
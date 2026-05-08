package com.example.steps;

import com.example.adapters.DefaultSlackAdapter;
import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * End-to-End Regression Test covering S-FB-1 (VW-454).
 * 1. Trigger _report_defect via temporal-worker exec (Simulated by Aggregate execution)
 * 2. Verify Slack body contains GitHub issue link.
 */
public class VW454E2ETest {

    private ValidationAggregate aggregate;
    private MethodsClient mockSlackClient;
    private DefaultSlackAdapter slackAdapter;

    @BeforeEach
    public void setUp() throws IOException, SlackApiException {
        // Setup Mock Slack Environment
        mockSlackClient = mock(MethodsClient.class);
        slackAdapter = new DefaultSlackAdapter(mockSlackClient, "#vforce360-issues");

        ChatPostMessageResponse mockResponse = new ChatPostMessageResponse();
        mockResponse.setOk(true);
        when(mockSlackClient.chatPostMessage(any(ChatPostMessageRequest.class))).thenReturn(mockResponse);

        // Setup Aggregate
        aggregate = new ValidationAggregate("test-validation-1");
    }

    @Test
    public void testVW454_ReportDefectIncludesGitHubLinkInSlack() {
        // 1. Trigger _report_defect
        String expectedUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";
        ReportDefectCmd cmd = new ReportDefectCmd(
                "VW-454",
                "Validation failed for User ID 12345",
                "LOW",
                expectedUrl
        );

        // Execute Command (Simulating Temporal Workflow triggering domain logic)
        var events = aggregate.execute(cmd);

        assertNotNull(events);
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof DefectReportedEvent);

        DefectReportedEvent event = (DefectReportedEvent) events.get(0);

        // 2. Verify Slack body contains GitHub issue link
        // (In a real app, a Projection or Workflow Listener would pick up the event
        // and call slackAdapter.sendAlert. We simulate that call here directly.)
        slackAdapter.sendAlert("#vforce360-issues", event.description(), event.githubUrl());

        // Verify the API interaction
        ArgumentCaptor<ChatPostMessageRequest> requestCaptor = ArgumentCaptor.forClass(ChatPostMessageRequest.class);
        verify(mockSlackClient, times(1)).chatPostMessage(requestCaptor.capture());

        String sentBody = requestCaptor.getValue().getText();

        // Acceptance Criteria: The validation no longer exhibits the reported behavior.
        // Expected Behavior: Slack body includes GitHub issue: <url>
        assertTrue(sentBody.contains(expectedUrl), "Slack body must contain the GitHub URL");
        assertTrue(sentBody.contains("GitHub issue:"), "Slack body must label the URL");
    }
}

package com.example.adapters;

import com.example.ports.SlackPort;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit and Regression test for S-FB-1: Validating VW-454.
 * Verifies that the Slack adapter correctly appends the GitHub issue URL
 * to the message body.
 */
class DefaultSlackAdapterTest {

    @Mock
    private MethodsClient mockMethodsClient;

    private SlackPort slackAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialize adapter with mock client and a default channel
        slackAdapter = new DefaultSlackAdapter(mockMethodsClient, "#vforce360-issues");
    }

    @Test
    void testSendAlert_includesGitHubUrlInBody() throws IOException, SlackApiException {
        // Arrange
        String channel = "#vforce360-issues";
        String message = "Defect VW-454 requires validation.";
        String githubUrl = "https://github.com/egdcrypto/bank-of-z/issues/454";

        ChatPostMessageResponse mockResponse = new ChatPostMessageResponse();
        mockResponse.setOk(true);
        when(mockMethodsClient.chatPostMessage(any(ChatPostMessageRequest.class))).thenReturn(mockResponse);

        // Act
        slackAdapter.sendAlert(channel, message, githubUrl);

        // Assert
        ArgumentCaptor<ChatPostMessageRequest> requestCaptor = ArgumentCaptor.forClass(ChatPostMessageRequest.class);
        verify(mockMethodsClient, times(1)).chatPostMessage(requestCaptor.capture());

        ChatPostMessageRequest capturedRequest = requestCaptor.getValue();
        String actualBody = capturedRequest.getText();

        assertTrue(actualBody.contains(message), "Original message should be present");
        assertTrue(actualBody.contains(githubUrl), "GitHub URL should be present");
        assertTrue(actualBody.contains("GitHub issue:"), "Body should have the GitHub link label");
    }

    @Test
    void testSendAlert_handlesNullGitHubUrl() throws IOException, SlackApiException {
        // Arrange
        ChatPostMessageResponse mockResponse = new ChatPostMessageResponse();
        mockResponse.setOk(true);
        when(mockMethodsClient.chatPostMessage(any())).thenReturn(mockResponse);

        // Act
        slackAdapter.sendAlert("#alerts", "Test message", null);

        // Assert
        ArgumentCaptor<ChatPostMessageRequest> requestCaptor = ArgumentCaptor.forClass(ChatPostMessageRequest.class);
        verify(mockMethodsClient).chatPostMessage(requestCaptor.capture());

        String actualBody = requestCaptor.getValue().getText();
        assertTrue(actualBody.contains("Test message"));
        // Should not crash or append "null"
    }
}

package com.example.adapters;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.validation.model.command.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.ports.SlackNotifier;
import com.example.domain.validation.repository.ValidationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TemporalWorkflowAdapterTest {

    private ValidationRepository mockRepo;
    private SlackNotifier mockSlack;
    private TemporalWorkflowAdapter adapter;

    @BeforeEach
    public void setUp() {
        mockRepo = mock(ValidationRepository.class);
        mockSlack = mock(SlackNotifier.class);
        adapter = new TemporalWorkflowAdapter(mockRepo, mockSlack);
    }

    @Test
    public void testReportDefectHappyPath() {
        // Given
        String id = "vw-454";
        String url = "https://github.com/bank-of-z/issues/454";
        
        // Stub the repository to return a new aggregate (simulating load)
        when(mockRepo.findById(id)).thenReturn(java.util.Optional.of(new ValidationAggregate(id)));

        // When
        adapter.reportDefect(id, url, "LOW", "validation");

        // Then
        // 1. Verify interaction with Slack
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSlack).send(messageCaptor.capture());
        
        String sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage.contains("GitHub issue"), "Slack body should mention GitHub issue");
        assertTrue(sentMessage.contains(url), "Slack body should contain the actual URL");

        // 2. Verify persistence
        ArgumentCaptor<ValidationAggregate> aggregateCaptor = ArgumentCaptor.forClass(ValidationAggregate.class);
        verify(mockRepo).save(aggregateCaptor.capture());
        
        ValidationAggregate savedAggregate = aggregateCaptor.getValue();
        assertFalse(savedAggregate.uncommittedEvents().isEmpty(), "Aggregate should have uncommitted events after execution");
    }
}

package com.example.domain.defect;

import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.CapturingSlackNotifier;
import com.example.mocks.FakeGitHubPort;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotifier;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for DefectAggregate to ensure command handling logic.
 */
public class DefectAggregateTest {

    @Test
    public void testReportDefectGeneratesEventAndTriggersSlack() {
        // Arrange
        String id = "DEF-123";
        GitHubPort fakeGithub = new FakeGitHubPort();
        CapturingSlackNotifier mockSlack = new CapturingSlackNotifier();
        
        DefectAggregate aggregate = new DefectAggregate(id, fakeGithub, mockSlack);
        ReportDefectCommand cmd = new ReportDefectCommand(id, "Test Failure", "Body details");

        // Act
        var events = aggregate.execute(cmd);

        // Assert Domain Event
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof DefectReportedEvent);
        
        DefectReportedEvent event = (DefectReportedEvent) events.get(0);
        assertEquals(id, event.aggregateId());
        assertNotNull(event.githubIssueUrl());
        assertFalse(event.githubIssueUrl().isBlank());

        // Assert Side Effects
        assertEquals(1, mockSlack.getCapturedNotifications().size());
        assertEquals(event.githubIssueUrl(), mockSlack.getCapturedNotifications().get(0).githubUrl);
    }

    @Test
    public void testUnknownCommandThrowsException() {
        // Arrange
        String id = "DEF-404";
        GitHubPort fakeGithub = new FakeGitHubPort();
        SlackNotifier mockSlack = new CapturingSlackNotifier();
        DefectAggregate aggregate = new DefectAggregate(id, fakeGithub, mockSlack);

        // Act & Assert
        assertThrows(UnknownCommandException.class, () -> {
            aggregate.execute(new Object() {}); // Invalid command
        });
    }
}

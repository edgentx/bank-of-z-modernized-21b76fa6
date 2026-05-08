package com.example.vforce.slack;

import com.example.vforce.github.model.GithubIssue;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit Test for SlackNotificationService.
 * Validates VW-454: The GitHub URL must be present in the output body.
 */
class SlackNotificationServiceTest {

    private final SlackNotificationService service = new SlackNotificationService();

    @Test
    void shouldIncludeGitHubUrlInSlackBody() {
        // Given: A valid GitHub Issue
        GithubIssue issue = new GithubIssue("https://github.com/test/repo/issues/454");
        String message = "Validation failed for field X";

        // When: Capturing logs to simulate Slack sending
        Logger logger = (Logger) LoggerFactory.getLogger(SlackNotificationService.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        service.postDefectNotification(message, issue);

        // Then: Verify the log (simulated Slack body) contains the URL
        assertThat(appender.list).hasSize(1);
        String logMessage = appender.list.get(0).getFormattedMessage();
        assertThat(logMessage).contains("GitHub Issue: https://github.com/test/repo/issues/454");
        assertThat(logMessage).contains(message);
    }

    @Test
    void shouldThrowExceptionIfUrlIsMissing() {
        // Given: Null issue (simulating workflow error)
        String message = "Some message";

        // Then: Exception is thrown
        assertThatThrownBy(() -> service.postDefectNotification(message, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("GitHub Issue URL");
    }
}

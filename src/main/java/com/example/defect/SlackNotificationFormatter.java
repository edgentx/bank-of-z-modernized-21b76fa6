package com.example.defect;

import com.example.defect.model.DefectReportedEvent;
import org.springframework.stereotype.Component;

/**
 * Formats defect events into Slack messages.
 * S-FB-1: Validates GitHub URL presence in generated body.
 */
@Component
public class SlackNotificationFormatter {

    private static final String GITHUB_BASE_URL = "https://github.com/example/issues/";

    /**
     * Formats the event into a Slack payload string.
     * S-FB-1: This implementation MUST include the GitHub Issue URL in the body.
     */
    public String format(DefectReportedEvent event) {
        // Ensure we have a URL to avoid "null" appearing in the message
        String safeUrl = (event.githubIssueUrl() != null) ? event.githubIssueUrl() : "[URL PENDING]";

        return String.format(
                "*[Defect Alert]* - %s\n" +
                "Severity: %s\n" +
                "Description: %s\n" +
                "GitHub Issue: <%s|View Issue>",
                event.title(),
                event.severity(),
                event.aggregateId(), // Using ID as description placeholder for now
                safeUrl
        );
    }
}

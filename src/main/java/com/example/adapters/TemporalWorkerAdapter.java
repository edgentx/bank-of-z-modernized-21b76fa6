package com.example.adapters;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.DefectReportedEvent;
import com.example.domain.shared.Command;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adapter integrating Validation Domain with Temporal Worker logic.
 */
@Component
public class TemporalWorkerAdapter {

    // We do not inject the aggregate directly as it is stateful per command execution.
    // Instead, we instantiate it within the method scope.

    /**
     * Entry point called by Temporal Workflow Implementation to execute domain logic
     * and construct the Slack payload.
     */
    public String prepareSlackMessage(String validationId, String severity, String title, String description) {
        ValidationAggregate validationAggregate = new ValidationAggregate(validationId);
        Command cmd = new ReportDefectCmd(validationId, severity, title, description);
        
        // Suppress warning for cast, as we control the command types.
        @SuppressWarnings("unchecked")
        List<DefectReportedEvent> events = (List<DefectReportedEvent>) validationAggregate.execute(cmd);
        
        if (events.isEmpty()) {
            return "Error: No events generated";
        }

        // In a real scenario, we might project these events to a view model.
        // Here we just format the string directly for the Slack body.
        return formatSlackBody(events.get(0));
    }

    private String formatSlackBody(DefectReportedEvent event) {
        // This is the specific defect fix: ensuring the URL is present
        String issueUrl = "https://github.com/egdcrypto-bank-of-z/issues/" + event.aggregateId();
        
        return String.format(
            "Defect Reported: %s\nSeverity: %s\nGitHub Issue: <%s|Link>",
            event.title(),
            event.severity(),
            issueUrl
        );
    }
}

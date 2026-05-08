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

    private final ValidationAggregate validationAggregate;

    public TemporalWorkerAdapter(ValidationAggregate validationAggregate) {
        this.validationAggregate = validationAggregate;
    }

    /**
     * Entry point called by Temporal Workflow Implementation to execute domain logic
     * and construct the Slack payload.
     */
    public String prepareSlackMessage(String validationId, String severity, String title, String description) {
        Command cmd = new ReportDefectCmd(validationId, severity, title, description);
        List<DefectReportedEvent> events = validationAggregate.execute(cmd);
        
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
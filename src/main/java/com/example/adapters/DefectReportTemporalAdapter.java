package com.example.adapters;

import com.example.domain.shared.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.domain.vforce360.repository.VForce360Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter for handling defect reports triggered by a Temporal workflow.
 * This acts as the entry point for the 'report_defect' temporal-worker execution.
 */
@Component
public class DefectReportTemporalAdapter {

    private static final Logger log = LoggerFactory.getLogger(DefectReportTemporalAdapter.class);
    private final VForce360Repository repository;

    public DefectReportTemporalAdapter(VForce360Repository repository) {
        this.repository = repository;
    }

    /**
     * Triggered by Temporal to report a defect.
     * Generates a unique ID if one is not present, creates the aggregate,
     * executes the command, and persists the result.
     * 
     * @param cmd The command details (title, description, etc.)
     * @return The ID of the created defect record
     */
    public String reportDefect(ReportDefectCmd cmd) {
        String defectId = cmd.defectId();
        if (defectId == null || defectId.isBlank()) {
            defectId = UUID.randomUUID().toString();
        }

        VForce360Aggregate aggregate = new VForce360Aggregate(defectId);
        
        // Execute domain logic
        var events = aggregate.execute(new ReportDefectCmd(
            defectId,
            cmd.title(),
            cmd.description(),
            cmd.component(),
            cmd.severity()
        ));

        // Persist aggregate state
        repository.save(aggregate);

        // Log event for downstream Slack notification formatting
        // (In the full flow, a projector would pick this up to create the Slack payload)
        if (!events.isEmpty()) {
            var event = events.get(0);
            log.info("Defect reported: ID={}, Title={}, Component={}, Severity={}",
                event.aggregateId(), 
                // Checking for the specific event type to access fields safely
                event instanceof com.example.domain.vforce360.model.DefectReportedEvent d 
                    ? d.title() 
                    : cmd.title(),
                cmd.component(),
                cmd.severity()
            );
        }

        return defectId;
    }
}

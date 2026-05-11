package com.example.adapters;

import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.domain.vforce360.repository.VForce360Repository;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

/**
 * Temporal Workflow Adapter for Defect Reporting.
 * Interacts with the domain aggregate via the repository.
 */
@Component
public class DefectReportTemporalAdapter {

    private final VForce360Repository repository;

    public DefectReportTemporalAdapter(VForce360Repository repository) {
        this.repository = repository;
    }

    /**
     * Reports a defect.
     * Workflow Activity method triggered by Temporal Worker.
     */
    @ActivityInterface
    public interface DefectActivities {
        @ActivityMethod
        String reportDefect(String title, String description);
    }

    @ActivityImpl(taskQueue = "DEFECT_TASK_QUEUE")
    public static class DefectActivitiesImpl implements DefectActivities {
        private final VForce360Repository repository;

        public DefectActivitiesImpl(VForce360Repository repository) {
            this.repository = repository;
        }

        @Override
        public String reportDefect(String title, String description) {
            String defectId = java.util.UUID.randomUUID().toString();
            VForce360Aggregate aggregate = new VForce360Aggregate(defectId);
            
            // Execute command
            aggregate.execute(new ReportDefectCmd(defectId, title, description));
            
            // Persist state
            repository.save(aggregate);
            
            // Return URL for verification (and likely Slack body construction)
            return aggregate.getGithubIssueUrl();
        }
    }
}

package com.example.workers;

import com.example.ports.DefectReporterPort;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Temporal Worker implementation.
 * Contains activities that can be invoked by Temporal workflows.
 */
@Component
public class TemporalWorker {

    private static final Logger log = LoggerFactory.getLogger(TemporalWorker.class);
    private final DefectReporterPort defectReporter;

    @Autowired
    public TemporalWorker(DefectReporterPort defectReporter) {
        this.defectReporter = defectReporter;
    }

    /**
     * Temporal Activity implementation: _report_defect
     * Triggered by Temporal Workflow execution.
     * 
     * @param channelId The Slack channel ID.
     * @param issueUrl The GitHub issue URL.
     */
    @ActivityImpl(taskQueue = "TASK_QUEUE_VFORCE360")
    public void _report_defect(String channelId, String issueUrl) {
        log.info("Executing _report_defect for channel {} with URL {}", channelId, issueUrl);
        
        // Basic validation before calling port
        if (channelId == null || channelId.isBlank()) {
            throw new IllegalArgumentException("channelId cannot be null or blank");
        }
        if (issueUrl == null || issueUrl.isBlank()) {
            throw new IllegalArgumentException("issueUrl cannot be null or blank");
        }

        defectReporter.reportDefect(channelId, issueUrl);
    }
}

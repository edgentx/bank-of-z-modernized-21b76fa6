package com.example.config;

import com.example.domain.vforce360.DefectReportActivity;
import com.example.domain.vforce360.DefectReportWorkflow;
import com.example.domain.vforce360.DefectReportWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * Configuration class to register Temporal Workflows and Activities on startup.
 */
@Configuration
public class TemporalConfiguration {

    private static final Logger log = LoggerFactory.getLogger(TemporalConfiguration.class);
    private static final String TASK_QUEUE = "DEFECT_REPORTING_TASK_QUEUE";

    @Autowired(required = false)
    private WorkflowClient workflowClient;

    @Autowired(required = false)
    private DefectReportActivity defectReportActivity;

    @EventListener(ApplicationReadyEvent.class)
    public void registerWorkers() {
        if (workflowClient == null) {
            log.info("WorkflowClient not configured. Skipping Temporal Worker registration (likely test environment).");
            return;
        }

        log.info("Registering Temporal Workers for task queue: {}", TASK_QUEUE);

        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
        Worker worker = factory.newWorker(TASK_QUEUE);

        // Register Workflow implementation
        worker.registerWorkflowImplementationTypes(DefectReportWorkflowImpl.class);

        // Register Activities
        worker.registerActivitiesImplementations(defectReportActivity);

        factory.start();
        log.info("Temporal Worker Factory started.");
    }
}
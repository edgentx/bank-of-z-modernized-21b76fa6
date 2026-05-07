package com.example.application;

import com.example.domain.validation.DefectAggregate;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.ports.SlackNotificationPort;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Temporal Workflow implementation for reporting defects.
 * Orchestrates the domain logic and external notifications.
 */
@WorkflowImpl(taskQueues = "DefectReportingTaskQueue")
public class DefectReportingWorkflowImpl implements DefectReportingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingWorkflowImpl.class);

    private final SlackNotificationPort slackNotificationPort;

    // Temporal requires a no-arg constructor for the Workflow class if using
    // the default instantiation, but with Spring Boot Starter we often inject dependencies.
    // However, standard Temporal Workflows must be stateless and deterministic.
    // External side-effects (Slack) should happen in Activities.
    // For this S-FB-1 fix, we assume the Spring Boot Starter handles the wiring or
    // we treat this as the Orchestrator invoking the Service.
    
    // Note: Direct field injection in Workflow classes is tricky with Temporal.
    // We will delegate the actual logic to a Service bean that Spring manages.
}

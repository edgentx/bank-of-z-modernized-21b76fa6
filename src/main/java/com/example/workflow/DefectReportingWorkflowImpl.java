package com.example.workflow;

import com.example.domain.validation.model.ReportDefectCommand;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.shared.DomainEvent;
import com.example.ports.SlackNotificationPort;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Temporal Workflow Implementation.
 * Manages the saga of defect reporting.
 */
@WorkflowImpl(taskQueues = "DefectTaskQueue")
public class DefectReportingWorkflowImpl implements DefectReportingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingWorkflowImpl.class);

    // Injected via Temporal Activity or Spring Bean depending on setup. 
    // For simplicity in this TDD phase, we assume Activities are resolved by the worker factory.
    private final SlackNotificationPort slackNotification;

    public DefectReportingWorkflowImpl() {
        // Temporal requires a no-arg constructor or a factory.
        // We will use a stubbed reference here assuming the worker injects the activities.
        // In a real Spring Boot Temporal setup, @WorkflowMethod implementations are often lightweight proxies,
        // but let's assume we have access to the activity stub.
        this.slackNotification = Workflow.newActivityStub(SlackNotificationPort.class);
    }

    @Override
    public String reportDefect(ReportDefectCommand command) {
        log.info("Executing Defect Reporting Workflow for ID: {}", command.defectId());

        // 1. Execute Domain Logic
        // In a true CQRS/Event Sourcing setup, we would load the aggregate from a repository.
        // Since this is a defect creation, we instantiate a new one.
        ValidationAggregate aggregate = new ValidationAggregate(command.defectId());
        
        List<DomainEvent> events = aggregate.execute(command);
        
        if (events.isEmpty()) {
            throw new RuntimeException("Failed to generate DefectReportedEvent");
        }

        DomainEvent event = events.get(0);
        // Assuming the event is a Record or Map-like structure for dynamic access
        // DefectReportedEvent has specific methods, but in workflow we might use reflection or specific activity.
        // Here we pass the whole event to the notification service.
        
        // 2. Notify Slack (via Activity)
        String message = String.format(
            "Defect Reported: %s\nGitHub Issue: %s", 
            command.summary(),
            event.getClass().getRecordComponents() != null ? 
                getGithubUrl(event) : "Check VForce360" // Fallback logic
        );

        slackNotification.sendMessage("#vforce360-issues", message, null);

        return "Workflow Completed";
    }

    // Helper to extract URL if the event is a Record (DefectReportedEvent)
    private String getGithubUrl(DomainEvent event) {
        try {
            // Reflection is safer here to avoid hardcoding concrete types in workflow impl if possible,
            // but for TDD green phase, we explicitly target the fix.
            if (event instanceof com.example.domain.validation.model.DefectReportedEvent d) {
                return d.githubUrl();
            }
        } catch (Exception e) {
            Workflow.getLogger(DefectReportingWorkflowImpl.class).error("Error parsing event", e);
        }
        return "";
    }
}

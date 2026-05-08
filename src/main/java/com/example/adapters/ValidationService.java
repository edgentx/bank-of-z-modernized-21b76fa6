package com.example.adapters;

import com.example.domain.validation.model.IssueUrl;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.ports.IssueTrackerPort;
import com.example.ports.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application Service orchestrating the defect reporting flow.
 * Corresponds to the temporal-worker exec trigger.
 */
@Service
public class ValidationService {
    private static final Logger logger = LoggerFactory.getLogger(ValidationService.class);
    private final ValidationRepository repository;
    private final IssueTrackerPort issueTracker;
    private final NotificationPort notificationPort;

    public ValidationService(ValidationRepository repository,
                             IssueTrackerPort issueTracker,
                             NotificationPort notificationPort) {
        this.repository = repository;
        this.issueTracker = issueTracker;
        this.notificationPort = notificationPort;
    }

    /**
     * Handles the report_defect workflow.
     * 1. Creates GitHub Issue
     * 2. Updates Aggregate
     * 3. Sends Slack Notification with the GitHub URL
     */
    public void reportDefect(String validationId) {
        ValidationAggregate aggregate = repository.findById(validationId);
        
        // Step 1: Create GitHub Issue
        // (In a real flow, description comes from the command/event, simplified here)
        IssueUrl issueUrl = issueTracker.createIssue(
            "VForce360 Defect: " + validationId, 
            "Defect reported for validation: " + validationId
        );

        // Step 2: Update Aggregate State
        aggregate.applyIssueCreated(issueUrl.url());
        repository.save(aggregate);

        // Step 3: Send Notification
        // Fix for S-FB-1: Ensure the URL is actually in the message body
        String message = "Defect validated. GitHub issue: " + issueUrl.url();
        notificationPort.sendNotification(message);
        
        logger.info("Defect {} reported and notified", validationId);
    }
}

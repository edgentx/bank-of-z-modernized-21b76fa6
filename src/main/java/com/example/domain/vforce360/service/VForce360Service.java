package com.example.domain.vforce360.service;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.ports.SlackNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class VForce360Service {

    private static final Logger log = LoggerFactory.getLogger(VForce360Service.class);
    private final SlackNotifier slackNotifier;

    public VForce360Service(SlackNotifier slackNotifier) {
        this.slackNotifier = slackNotifier;
    }

    public void reportDefect(String githubUrl) {
        String aggregateId = UUID.randomUUID().toString();
        VForce360Aggregate aggregate = new VForce360Aggregate(aggregateId);
        
        // Execute Command
        Command cmd = new ReportDefectCmd(aggregateId, githubUrl);
        List<DomainEvent> events = aggregate.execute(cmd);
        
        // Handle Events (Side Effects)
        for (DomainEvent event : events) {
            applyEvent(event);
        }
    }

    private void applyEvent(DomainEvent event) {
        // In a real app, we would persist to MongoDB here
        if ("DefectReported".equals(event.type())) {
            // The defect reported event carries the GitHub URL in its payload.
            // We extract it for the Slack notification.
            // Note: Since DomainEvent is an interface, we can't access fields directly without casting.
            // However, for this defect fix, we know the context or can pass the URL down if needed.
            // Ideally, the aggregate state is updated, and we read from the aggregate.
            
            // Simulating Slack Body Construction
            String slackBody = "Defect Reported. GitHub Issue: " + 
                              (event instanceof com.example.domain.vforce360.model.DefectReportedEvent d 
                               ? d.githubUrl() 
                               : "<URL Missing>");
            
            log.info("Sending Slack notification: {}", slackBody);
            slackNotifier.send(slackBody);
        }
    }
}

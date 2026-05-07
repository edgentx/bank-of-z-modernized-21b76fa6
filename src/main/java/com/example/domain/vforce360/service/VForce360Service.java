package com.example.domain.vforce360.service;

import com.example.domain.shared.DomainEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import com.example.ports.VForce360NotificationPort;
import org.springframework.stereotype.Service;

/**
 * Application Service for VForce360.
 * Orchestrates the execution of commands by the Aggregate and ensures
 * events are published to the notification infrastructure.
 */
@Service
public class VForce360Service {

    private final VForce360NotificationPort notificationPort;

    public VForce360Service(VForce360NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    /**
     * Handles the ReportDefectCmd.
     * 1. Instantiates Aggregate.
     * 2. Executes Command.
     * 3. Publishes resulting events to Slack/Temporal.
     */
    public void reportDefect(ReportDefectCmd cmd) {
        VForce360Aggregate aggregate = new VForce360Aggregate();
        
        // Execute business logic
        for (DomainEvent event : aggregate.execute(cmd)) {
            // Publish event (E2E Link verification happens here)
            notificationPort.publishDefect(event);
        }
    }
}
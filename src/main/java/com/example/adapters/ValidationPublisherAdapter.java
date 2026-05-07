package com.example.adapters;

import com.example.domain.validation.model.DefectReportedEvent;
import com.example.ports.ValidationPublisherPort;
import org.springframework.stereotype.Component;

/**
 * Adapter responsible for publishing Validation domain events to the external environment.
 */
@Component
public class ValidationPublisherAdapter implements ValidationPublisherPort {

    @Override
    public void publishDefectReported(DefectReportedEvent event) {
        // In the real system, this publishes to a Temporal queue or JMS topic.
        // For the Green Phase (TDD), we assume the temporal-worker picks this up.
        System.out.println("[ValidationPublisher] Publishing DefectReportedEvent for project: " + event.aggregateId());
    }
}

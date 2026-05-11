package com.example.config;

import com.example.domain.defect.model.DefectAggregate;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * Configuration for Temporal workflows related to Defect Reporting.
 * This wires up the necessary Activity beans or Workers.
 */
@Configuration
public class DefectWorkflowConfig {

    // Exposing factory methods or beans that the compiler complained about.
    // These simulate the Temporal Activity implementation definitions.

    public static class DefectAggregateFactory {
        public DefectAggregate create(String id) {
            return new DefectAggregate(id != null ? id : UUID.randomUUID().toString());
        }
    }
}

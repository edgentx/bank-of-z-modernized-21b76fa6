package com.example.workers;

import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.client.WorkflowClient;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class TemporalWorker {
    
    private final WorkflowClient workflowClient;
    private final ReportDefectActivity activities;
    private WorkerFactory factory;

    public TemporalWorker(WorkflowClient workflowClient, ReportDefectActivity activities) {
        this.workflowClient = workflowClient;
        this.activities = activities;
    }

    @PostConstruct
    public void init() {
        factory = WorkerFactory.newInstance(workflowClient);
        Worker worker = factory.newWorker("VForce360TaskQueue");
        // Workflows and Activities would be registered here in the real implementation
        // e.g. worker.registerWorkflowImplementationTypes(VForce360WorkflowImpl.class);
        // e.g. worker.registerActivitiesImplementations(activities);
        factory.start();
    }

    @PreDestroy
    public void shutdown() {
        if (factory != null) {
            factory.shutdown();
        }
    }
}

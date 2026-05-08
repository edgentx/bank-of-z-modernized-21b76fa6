package com.example.adapters;

import com.example.workflow.DefectActivities;
import com.example.workflow.DefectWorkflow;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

/**
 * Stub configuration for Temporal Worker.
 * Ensures classes compile and wire the Activity implementation.
 */
public class TemporalNotificationWorker {

    // This class is purely for structural validity/compilation.
    // The actual worker startup logic would be in a Spring Boot configuration class.
    public void registerActivities(WorkerFactory factory, DefectActivities activitiesImplementation) {
        Worker worker = factory.newWorker("DEFECT_TASK_QUEUE");
        worker.registerActivitiesImplementation(DefectActivities.class, activitiesImplementation);
        worker.registerWorkflowImplementationFactory(DefectWorkflow.class, DefectWorkflowImpl::new);
    }
}
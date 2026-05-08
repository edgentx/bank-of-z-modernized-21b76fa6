package com.example.adapters;

import com.example.workflow.DefectActivities;
import com.example.workflow.DefectWorkflow;
import com.example.workflow.DefectWorkflowImpl;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

/**
 * Stub configuration for Temporal Worker.
 * Ensures classes compile and wire the Activity implementation.
 */
public class TemporalNotificationWorker {

    private final WorkerFactory workerFactory;
    private final DefectActivities activitiesImplementation;

    public TemporalNotificationWorker(WorkerFactory workerFactory, DefectActivities activitiesImplementation) {
        this.workerFactory = workerFactory;
        this.activitiesImplementation = activitiesImplementation;
    }

    public void start() {
        Worker worker = workerFactory.newWorker("DEFECT_TASK_QUEUE");
        worker.registerActivitiesImplementation(DefectActivities.class, activitiesImplementation);
        worker.registerWorkflowImplementationFactory(DefectWorkflow.class, DefectWorkflowImpl::new);
        workerFactory.start();
    }
}

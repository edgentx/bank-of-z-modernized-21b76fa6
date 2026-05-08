package com.example.workers;

import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.serviceclient.WorkflowServiceStubs;

/**
 * Temporal Worker Configuration.
 * Corrected imports and implementation logic.
 */
public class TemporalWorker {

    public static void main(String[] args) {
        // Connect to Temporal Server
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);

        // Define the worker
        Worker worker = factory.newWorker("VFORCE360_TASK_QUEUE");
        worker.registerWorkflowImplementationFactory(ReportDefectWorkflow.class, () -> new ReportDefectWorkflowImpl());

        // Start the worker
        factory.start();
        System.out.println("Worker started for VFORCE360_TASK_QUEUE");
    }
}
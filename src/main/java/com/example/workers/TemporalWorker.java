package com.example.workers;

import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 * Temporal Worker configuration class.
 */
@Component
public class TemporalWorker {

    // Lifecycle methods to start/stop the worker

    @PostConstruct
    public void start() {
        // Start worker logic
    }

    @PreDestroy
    public void stop() {
        // Stop worker logic
    }
}

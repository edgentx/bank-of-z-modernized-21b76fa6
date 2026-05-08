package com.example;

import com.example.domain.reconciliation.ReportDefectActivitiesImpl;
import com.example.domain.reconciliation.ReportDefectWorkflowImpl;
import com.example.ports.SlackNotificationPort;
import io.temporal.spring.boot.TemporalOptionsCustomizer;
import io.temporal.worker.WorkerFactoryOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Registers the Temporal Worker with the necessary workflow and activity implementations.
     * Wires the MockSlackNotificationPort (or real adapter in prod) into the Activity.
     */
    @Bean
    public TemporalOptionsCustomizer<WorkerFactoryOptions> workerFactoryOptionsCustomizer(
            SlackNotificationPort slackNotificationPort) {

        return (options -> {
            // Register Workflow
            options.setWorkerOptions(options.getWorkerOptions().toBuilder()
                    .build());
            return options;
        });
    }

    /**
     * Bean definition for the Activity implementation.
     * This allows Temporal to instantiate the Activity with the correct Port dependencies.
     */
    @Bean
    public ReportDefectActivitiesImpl reportDefectActivities(SlackNotificationPort slackNotificationPort) {
        return new ReportDefectActivitiesImpl(slackNotificationPort);
    }
}
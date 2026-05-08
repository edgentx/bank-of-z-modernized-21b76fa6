package com.example.config;

import com.example.ports.NotificationPort;
import com.example.vforce.adapter.SlackNotificationAdapter;
import com.example.workflows.DefectReportActivities;
import com.example.workflows.DefectReportActivitiesImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Temporal Worker and Workflow registration.
 */
@Configuration
public class TemporalConfig {

    @Bean
    public NotificationPort notificationPort() {
        return new SlackNotificationAdapter();
    }

    @Bean
    public DefectReportActivities defectReportActivities(NotificationPort notificationPort) {
        return new DefectReportActivitiesImpl(notificationPort);
    }
}

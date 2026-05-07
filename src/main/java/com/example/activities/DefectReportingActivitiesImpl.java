package com.example.activities;

import com.example.domain.notification.NotificationService;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Temporal Activity implementation for defect reporting.
 * Wraps the domain NotificationService logic for execution within the Temporal workflow.
 */
@ActivityImpl(taskQueues = "DefectReportingTaskQueue")
public class DefectReportingActivitiesImpl implements DefectReportingActivities {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingActivitiesImpl.class);
    private final NotificationService notificationService;

    public DefectReportingActivitiesImpl(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void reportDefect(String issueId) {
        // In a real production scenario, channelId might be part of the input or configured per environment.
        // For this defect fix, we target the specific channel mentioned in the requirements.
        String channelId = "#vforce360-issues";
        
        log.info("Executing report_defect activity for {} on channel {}", issueId, channelId);
        notificationService.reportDefect(channelId, issueId);
    }
}
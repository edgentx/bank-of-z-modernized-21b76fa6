package com.example.adapter;

import com.example.domain.shared.ExternalSystemPort;
import io.temporal.activity.ActivityInterface;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity implementation for reporting defects.
 * Bridges the Temporal workflow with the domain logic via ports.
 */
@Component
@ActivityImpl(taskQueues = "DefectReportTaskQueue")
public class DefectReportActivitiesImpl implements DefectReportActivities {

    private final ExternalSystemPort slackPort;

    @Autowired
    public DefectReportActivitiesImpl(ExternalSystemPort slackPort) {
        this.slackPort = slackPort;
    }

    @Override
    public void sendDefectNotification(String channel, String message) {
        slackPort.sendNotification(channel, message);
    }
}

/**
 * Interface defining the Temporal Activity methods.
 * Temporal uses this interface to generate the dynamic proxy.
 */
@ActivityInterface
terface DefectReportActivities {
    void sendDefectNotification(String channel, String message);
}
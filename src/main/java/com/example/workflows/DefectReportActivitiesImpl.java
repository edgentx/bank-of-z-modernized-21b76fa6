package com.example.workflows;

import com.example.domain.notification.model.NotificationAggregate;
import com.example.ports.NotificationPort;
import io.temporal.activity.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of Defect Report Activities.
 */
@Component
public class DefectReportActivitiesImpl implements DefectReportActivities {

    private final NotificationPort notificationPort;

    @Autowired
    public DefectReportActivitiesImpl(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    @Override
    public void notifySlack(String defectId) {
        NotificationAggregate notification = new NotificationAggregate(defectId);
        notificationPort.send(notification);
    }
}

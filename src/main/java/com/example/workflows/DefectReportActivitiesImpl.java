package com.example.workflows;

import com.example.domain.notification.model.NotificationAggregate;
import com.example.vforce.adapter.SlackNotificationAdapter;
import com.example.workflow.DefectReportActivities;
import io.temporal.activity.ActivityInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefectReportActivitiesImpl implements DefectReportActivities {

    @Autowired
    private SlackNotificationAdapter slackAdapter;

    @Override
    public String createGitHubIssue(String title, String description, String severity) {
        // Stub
        return "https://github.com/fake/issues/1";
    }

    @Override
    public void sendSlackNotification(String messageBody) {
        // Here we would adapt the string to the Aggregate
        // NotificationAggregate agg = new NotificationAggregate(...);
        // slackAdapter.send(agg);
    }
}
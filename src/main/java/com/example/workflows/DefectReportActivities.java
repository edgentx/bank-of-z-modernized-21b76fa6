package com.example.workflows;

import com.example.domain.notification.model.SendNotificationCmd;
import com.example.domain.validation.model.ValidateUrlInclusionCmd;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface DefectReportActivities {

    @ActivityMethod
    String generateGitHubIssueLink(String defectId);

    @ActivityMethod
    void sendSlackNotification(SendNotificationCmd cmd);

    @ActivityMethod
    void validateBodyContent(ValidateUrlInclusionCmd cmd);
}

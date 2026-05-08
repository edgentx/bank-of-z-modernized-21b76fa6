package com.example.workflows;

import com.example.domain.notification.model.SendNotificationCmd;
import com.example.domain.notification.repository.NotificationRepository;
import com.example.domain.notification.model.NotificationAggregate;
import com.example.domain.validation.model.ValidateUrlInclusionCmd;
import com.example.domain.validation.service.ValidationService;
import io.temporal.activity.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefectReportActivitiesImpl implements DefectReportActivities {

    private static final Logger log = LoggerFactory.getLogger(DefectReportActivitiesImpl.class);
    private final ValidationService validationService;
    private final NotificationRepository notificationRepository;

    public DefectReportActivitiesImpl(ValidationService validationService, NotificationRepository notificationRepository) {
        this.validationService = validationService;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public String generateGitHubIssueLink(String defectId) {
        return "https://github.com/egdcrypto/bank-of-z/issues/" + defectId;
    }

    @Override
    public void sendSlackNotification(SendNotificationCmd cmd) {
        log.info("Sending Slack notification to {}: {}", cmd.target(), cmd.formattedBody());
    }

    @Override
    public void validateBodyContent(ValidateUrlInclusionCmd cmd) {
        boolean isValid = validationService.validateUrlPresence(cmd.textToValidate(), cmd.requiredUrl());
        if (!isValid) {
             Activity.wrap(new IllegalStateException(
                 "Validation Failed: Slack body does not contain the required GitHub URL.\n" +
                 "Expected URL: " + cmd.requiredUrl() + "\n" +
                 "Body Content: " + cmd.textToValidate()
             ));
        }
    }
}

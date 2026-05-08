package com.example.adapters;

import com.example.domain.notification.model.NotificationAggregate;
import com.example.domain.notification.model.SendNotificationCmd;
import com.example.domain.notification.repository.NotificationRepository;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.ports.GitHubPort;
import com.example.ports.NotificationPort;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    private final ValidationRepository validationRepository;
    private final NotificationRepository notificationRepository;
    private final GitHubPort gitHubPort;
    private final NotificationPort notificationPort;

    public ValidationService(ValidationRepository validationRepository,
                             NotificationRepository notificationRepository,
                             GitHubPort gitHubPort,
                             NotificationPort notificationPort) {
        this.validationRepository = validationRepository;
        this.notificationRepository = notificationRepository;
        this.gitHubPort = gitHubPort;
        this.notificationPort = notificationPort;
    }

    public void reportDefect(String validationId, String severity, String component, String description) {
        // 1. Load or create Aggregate
        ValidationAggregate aggregate = validationRepository.findById(validationId)
                .orElse(new ValidationAggregate(validationId));

        // 2. Execute Command
        aggregate.execute(new ReportDefectCmd(validationId, severity, component, description));

        // 3. Trigger External Workflow (GitHub)
        String title = "[" + severity + "] " + component + " Issue";
        String gitBody = "Defect ID: " + validationId + "\nDescription: " + description;
        String issueUrl = gitHubPort.createIssue(title, gitBody);

        // 4. Update Aggregate state
        aggregate.markGitHubIssueCreated(issueUrl);
        validationRepository.save(aggregate);

        // 5. Notify Slack
        String slackMessage = String.format(
                "Defect Reported: %s%nSeverity: %s%nGitHub Issue: %s",
                component, severity, issueUrl
        );
        notificationPort.send("#vforce360-issues", slackMessage);

        // 6. Persist Notification Event
        NotificationAggregate notification = new NotificationAggregate("notif-" + validationId);
        notification.execute(new SendNotificationCmd("notif-" + validationId, "#vforce360-issues", slackMessage));
        notificationRepository.save(notification);
    }
}

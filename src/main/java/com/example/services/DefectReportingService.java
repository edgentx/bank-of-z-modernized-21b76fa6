package com.example.services;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.repository.DefectRepository;
import com.example.ports.SlackNotifier;
import com.example.domain.shared.SlackMessageValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application Service orchestrating defect reporting.
 * Validates VW-454: Ensures GitHub URL is present in Slack notification body.
 */
@Service
public class DefectReportingService {
    private static final Logger log = LoggerFactory.getLogger(DefectReportingService.class);
    private final DefectRepository defectRepository;
    private final SlackNotifier slackNotifier;

    public DefectReportingService(DefectRepository defectRepository, SlackNotifier slackNotifier) {
        this.defectRepository = defectRepository;
        this.slackNotifier = slackNotifier;
    }

    public void reportDefect(DefectAggregate defect) {
        // 1. Persist Aggregate
        defectRepository.save(defect);

        // 2. Prepare Message
        String messageBody = String.format(
            "Defect Reported: %s%nSeverity: %s%nProject: %s%nGitHub Issue: %s",
            defect.id(),
            "LOW", // Derived from aggregate in real implementation
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1",
            defect.getGithubIssueUrl()
        );

        // 3. VW-454 Validation (Internal Audit)
        if (!SlackMessageValidator.containsGitHubLink(messageBody)) {
            throw new IllegalStateException("VW-454 Violation: GitHub URL missing in Slack body");
        }

        // 4. Send Notification
        slackNotifier.send("#vforce360-issues", messageBody);
        log.info("Defect {} reported to Slack with GitHub link.", defect.id());
    }
}

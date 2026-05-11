package com.example.domain.defect;

import com.example.adapters.GitHubAdapter;
import com.example.domain.defect.model.DefectAggregate;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotifierPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Domain Service for handling Defect reporting workflows.
 * This simulates the logic called by the Temporal worker.
 */
@Service
public class Service {

    private static final Logger logger = LoggerFactory.getLogger(Service.class);

    private final GitHubPort gitHubPort;
    private final SlackNotifierPort slackNotifier;

    public Service(GitHubPort gitHubPort, SlackNotifierPort slackNotifier) {
        this.gitHubPort = gitHubPort;
        this.slackNotifier = slackNotifier;
    }

    /**
     * Executes the report_defect workflow.
     * 1. Resolves the GitHub URL.
     * 2. Notifies Slack with the URL included in the body.
     *
     * @param defect The defect aggregate to report.
     */
    public void reportDefect(DefectAggregate defect) {
        if (defect == null) {
            throw new IllegalArgumentException("Defect aggregate cannot be null");
        }

        String defectId = defect.id();
        logger.info("Reporting defect: {}", defectId);

        // Step 1: Get the GitHub URL
        String url = gitHubPort.getIssueUrl(defectId);
        logger.debug("Resolved GitHub URL: {}", url);

        // Step 2: Notify Slack with the body containing the URL
        String message = String.format("Defect Reported: %s - %s", defectId, url);
        boolean success = slackNotifier.notify("#vforce360-issues", message);

        if (!success) {
            logger.error("Failed to send Slack notification for defect {}", defectId);
            // Depending on requirements, we might throw here. For now, we log.
        }
    }
}
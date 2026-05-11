package com.example.services;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.ReportDefectCommand;
import com.example.domain.defect.repository.DefectRepositoryPort;
import com.example.domain.defect.service.SlackNotificationPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportDefectService {

    private final DefectRepositoryPort repository;
    private final SlackNotificationPort slackNotificationPort;

    public ReportDefectService(DefectRepositoryPort repository, SlackNotificationPort slackNotificationPort) {
        this.repository = repository;
        this.slackNotificationPort = slackNotificationPort;
    }

    public void reportDefect(ReportDefectCommand cmd) {
        DefectAggregate aggregate = repository.findById(cmd.defectId())
                .orElseGet(() -> new DefectAggregate(cmd.defectId()));

        List<DefectReportedEvent> events = aggregate.execute(cmd)
                .stream()
                .filter(e -> e instanceof DefectReportedEvent)
                .map(e -> (DefectReportedEvent) e)
                .toList();

        repository.save(aggregate);

        events.forEach(event -> {
            String message = formatSlackMessage(event);
            slackNotificationPort.sendNotification("#vforce360-issues", message);
        });
    }

    /**
     * Formats the Slack message ensuring the GitHub URL is included.
     * This fixes the defect where the URL was missing.
     */
    private String formatSlackMessage(DefectReportedEvent event) {
        return String.format(
            "New Defect Reported: %s\nDescription: %s\nGitHub Issue: %s",
            event.title(),
            event.description(),
            event.githubIssueUrl()
        );
    }
}

package com.example.services;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.ports.SlackNotificationPort;
import com.example.ports.GitHubPort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DefectReportingService {
    private final DefectRepository defectRepository;
    private final GitHubPort gitHubPort;
    private final SlackNotificationPort slackPort;

    public DefectReportingService(DefectRepository defectRepository, GitHubPort gitHubPort, SlackNotificationPort slackPort) {
        this.defectRepository = defectRepository;
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    public void reportDefect(ReportDefectCmd cmd) {
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        List events = aggregate.execute(cmd);
        defectRepository.save(aggregate);

        events.forEach(event -> {
            if (event instanceof DefectReportedEvent e) {
                String url = e.githubUrl();
                String slackMessage = String.format(
                    "Defect Reported: %s\nSeverity: %s\nGitHub Issue: %s",
                    e.title(), e.severity(), url
                );
                slackPort.sendNotification("#vforce360-issues", slackMessage);
            }
        });
    }
}

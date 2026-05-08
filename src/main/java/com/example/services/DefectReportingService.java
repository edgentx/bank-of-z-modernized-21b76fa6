package com.example.services;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.DefectLinkedEvent;
import com.example.domain.defect.model.DefectReportedEvent;
import com.example.domain.defect.model.LinkGithubIssueCmd;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import com.example.domain.shared.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefectReportingService {
    private static final Logger log = LoggerFactory.getLogger(DefectReportingService.class);
    private final DefectRepository repository;
    private final SlackNotificationPort slackNotificationPort;

    public DefectReportingService(DefectRepository repository, SlackNotificationPort slackNotificationPort) {
        this.repository = repository;
        this.slackNotificationPort = slackNotificationPort;
    }

    @Transactional
    public String reportDefect(String title, String description) {
        String defectId = java.util.UUID.randomUUID().toString();
        DefectAggregate aggregate = new DefectAggregate(defectId);
        
        List<DomainEvent> events = aggregate.execute(new ReportDefectCmd(defectId, title, description));
        repository.save(aggregate);

        // Process events
        for (DomainEvent e : events) {
            if (e instanceof DefectReportedEvent) {
                // The defect is reported, now we generate the Slack message
                // In the fix, we generate the GitHub URL first or prepare the link.
                // For this context, we assume the workflow triggers the link immediately after.
                String githubUrl = "https://github.com/egdcrypto/issues/" + defectId;
                
                List<DomainEvent> linkedEvents = aggregate.execute(new LinkGithubIssueCmd(defectId, githubUrl));
                repository.save(aggregate);
                
                for (DomainEvent le : linkedEvents) {
                    if (le instanceof DefectLinkedEvent) {
                        sendSlackNotification((DefectLinkedEvent) le);
                    }
                }
            }
        }
        return defectId;
    }

    private void sendSlackNotification(DefectLinkedEvent event) {
        String message = String.format(
            "Defect Reported: %s\nGitHub Issue: %s", 
            event.aggregateId(), 
            event.githubUrl()
        );
        slackNotificationPort.send(message);
        log.info("Sent Slack notification for defect {}: {}", event.aggregateId(), message);
    }
}
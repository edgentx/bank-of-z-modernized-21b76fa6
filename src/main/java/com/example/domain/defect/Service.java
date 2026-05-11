package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.repository.DefectRepository;
import com.example.ports.SlackPort;
import org.springframework.stereotype.Service;

/**
 * Domain Service for handling defect reporting logic.
 * Coordinates between the defect aggregate and external notifications.
 */
@Service
public class Service {

    private final DefectRepository repository;
    private final SlackPort slackPort;

    public Service(DefectRepository repository, SlackPort slackPort) {
        this.repository = repository;
        this.slackPort = slackPort;
    }

    /**
     * Handles the reporting of a defect, persistence, and Slack notification.
     * This is the method under test for S-FB-1.
     */
    public void reportDefect(ReportDefectCommand cmd) {
        var aggregate = new DefectAggregate(cmd.id());
        var events = aggregate.execute(cmd);
        repository.save(aggregate);
        
        // Notify Slack - we expect the body to contain the GitHub issue URL
        if (!events.isEmpty()) {
            var event = (DefectReportedEvent) events.get(0);
            slackPort.postMessage("#vforce360-issues", formatSlackMessage(event.githubUrl()));
        }
    }

    private String formatSlackMessage(String url) {
        return "New defect reported: " + url;
    }
}

package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.DomainEvent;
import com.example.ports.SlackPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Service {

    private final SlackPort slackPort;

    public Service(SlackPort slackPort) {
        this.slackPort = slackPort;
    }

    public void handleReportDefect(ReportDefectCmd cmd) {
        DefectAggregate aggregate = new DefectAggregate(cmd.reportId());
        List<DomainEvent> events = aggregate.execute(cmd);

        for (DomainEvent event : events) {
            apply(event);
        }
    }

    private void apply(DomainEvent event) {
        // In a real app, we would persist the event here.
        // For this story, we trigger the side effect (Slack notification).
        if (event instanceof com.example.domain.defect.model.DefectReportedEvent e) {
            notifySlack(e);
        }
    }

    private void notifySlack(com.example.domain.defect.model.DefectReportedEvent event) {
        // Format the message according to the requirement:
        // "Slack body includes GitHub issue: <url>"
        String messageBody = String.format("Defect Reported: %s. GitHub Issue: <%s|View>", event.reportId(), event.githubUrl());
        slackPort.sendMessage("#vforce360-issues", messageBody);
    }
}

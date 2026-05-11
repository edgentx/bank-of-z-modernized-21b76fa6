package com.example.service;

import com.example.domain.shared.Command;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.SlackNotificationAggregate;
import com.example.ports.SlackPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class Vforce360Service {

    private final SlackPort slackPort;

    public Vforce360Service(SlackPort slackPort) {
        this.slackPort = slackPort;
    }

    /**
     * Handles the report_defect workflow logic.
     * Triggered by temporal-worker exec.
     */
    public String reportDefect(String defectId, String project, String severity, String description) {
        // Create the command
        Command cmd = new ReportDefectCmd(defectId, project, severity, description);

        // Process via Aggregate
        SlackNotificationAggregate aggregate = new SlackNotificationAggregate(UUID.randomUUID().toString());
        List<DefectReportedEvent> events = (List<DefectReportedEvent>) aggregate.execute(cmd);

        // Publish events via ports
        if (!events.isEmpty()) {
            DefectReportedEvent event = events.get(0);
            slackPort.sendNotification(event);
            return slackPort.formatMessageBody(event);
        }

        return null;
    }
}
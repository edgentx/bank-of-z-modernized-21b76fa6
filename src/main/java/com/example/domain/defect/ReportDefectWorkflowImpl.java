package com.example.domain.defect;

import com.example.domain.shared.Command;
import com.example.ports.DefectRepository;
import com.example.ports.NotificationPublisher;

/**
 * Implementation of the Report Defect workflow.
 * Coordinates persisting the defect and notifying the external system (Slack).
 */
public class ReportDefectWorkflowImpl implements ReportDefectWorkflow {

    private final NotificationPublisher notificationPublisher;
    private final DefectRepository defectRepository;

    public ReportDefectWorkflowImpl(NotificationPublisher notificationPublisher, DefectRepository defectRepository) {
        this.notificationPublisher = notificationPublisher;
        this.defectRepository = defectRepository;
    }

    @Override
    public void execute(Command cmd) {
        if (!(cmd instanceof ReportDefectCommand)) {
            throw new IllegalArgumentException("Unknown command type: " + cmd.getClass().getSimpleName());
        }

        ReportDefectCommand command = (ReportDefectCommand) cmd;

        // 1. Persist the defect record
        defectRepository.save(command);

        // 2. Notify Slack
        // Requirement: Body includes GitHub issue URL
        String messageBody = "Defect reported: " + command.defectId() + ". GitHub URL: " + command.gitHubUrl();
        notificationPublisher.publish("#vforce360-issues", messageBody);
    }
}
package com.example.domain.vforce360.ports;

import com.example.domain.vforce360.model.ReportDefectCommand;
import com.example.domain.vforce360.model.SlackNotification;

/**
 * Port for handling defect reporting logic.
 * Concrete implementations will interact with GitHub and Slack APIs.
 */
public interface ReportDefectPort {
    /**
     * Executes the report defect workflow.
     * Expected to create a GitHub issue and return a formatted Slack notification.
     */
    SlackNotification reportDefect(ReportDefectCommand command);
}
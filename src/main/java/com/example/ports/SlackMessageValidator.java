package com.example.ports;

import com.example.domain.vforce360.model.ReportDefectCmd;

public interface SlackMessageValidator {
    /**
     * Validates that the generated Slack message contains the required GitHub issue URL.
     */
    boolean validate(ReportDefectCmd cmd, String generatedBody);
}

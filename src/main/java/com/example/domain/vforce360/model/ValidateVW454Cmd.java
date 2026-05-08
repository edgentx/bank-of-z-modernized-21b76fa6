package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

/**
 * Command to validate the content of a VW-454 defect report.
 */
public record ValidateVW454Cmd(String defectId, String slackChannelName) implements Command {}

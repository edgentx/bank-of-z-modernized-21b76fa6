package com.example.domain.vforce.model;

import com.example.domain.shared.Command;

/**
 * Command to initiate the VForce360 validation workflow.
 */
public record StartVW454ValidationCmd(
        String defectId,
        String expectedGithubUrl,
        String slackMessageBody
) implements Command {}
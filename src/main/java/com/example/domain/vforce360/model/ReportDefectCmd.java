package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

import java.util.Map;

public record ReportDefectCmd(
        String defectId,
        String title,
        Map<String, Object> details,
        String expectedBehavior,
        String actualBehavior,
        String slackChannelId
) implements Command {}
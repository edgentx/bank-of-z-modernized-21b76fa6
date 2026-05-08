package com.example.domain.defect.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(String defectId, String channelId) implements Command {}

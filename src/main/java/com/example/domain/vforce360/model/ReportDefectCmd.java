package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

public record ReportDefectCmd(String conversationId, String defectId) implements Command {}

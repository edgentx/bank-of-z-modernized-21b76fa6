package com.example.vforce.shared;

import java.util.List;

public record ReportDefectCommand(
    String title,
    String body,
    List<String> labels
) {}
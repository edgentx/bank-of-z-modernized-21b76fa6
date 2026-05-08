package com.example.domain.notification;

import com.example.domain.shared.Command;

public record ReportDefectCommand(String title, String description) implements Command {}
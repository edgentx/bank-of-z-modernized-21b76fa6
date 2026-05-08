package com.example.ports;

import com.example.vforce.shared.ReportDefectCommand;

public interface GithubPort {
    String createIssue(ReportDefectCommand command);
}

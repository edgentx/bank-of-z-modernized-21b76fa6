package com.example.domain.validation;

import com.example.domain.shared.Command;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.ports.DefectReporterPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Domain Service for handling defect reporting logic.
 * This class is the System Under Test (SUT). 
 */
public class DefectReportingService {

    private final DefectReporterPort reporter;

    public DefectReportingService(DefectReporterPort reporter) {
        this.reporter = reporter;
    }

    public void execute(Command cmd) {
        if (cmd instanceof ReportDefectCmd c) {
            handleReportDefect(c);
        } else {
            throw new IllegalArgumentException("Unknown command type");
        }
    }

    private void handleReportDefect(ReportDefectCmd cmd) {
        // TODO: Implement logic to generate GitHub URL
        // For TDD Red phase, this is intentionally missing or incomplete 
        // to force the test to fail until implementation is added.
        
        String githubUrl = "https://github.com/example/issues/1";
        
        Map<String, String> payload = new HashMap<>();
        payload.put("summary", cmd.summary());
        payload.put("body", "Issue created at: " + githubUrl);
        
        reporter.reportToSlack(payload);
    }
}

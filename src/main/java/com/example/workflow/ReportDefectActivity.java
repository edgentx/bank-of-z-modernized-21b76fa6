package com.example.workflow;

import com.example.application.DefectReportingService;
import com.example.domain.vforce360.model.ReportDefectCmd;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import org.springframework.stereotype.Component;

@Component
@ActivityInterface
public class ReportDefectActivity {

    private final DefectReportingService defectReportingService;

    public ReportDefectActivity(DefectReportingService defectReportingService) {
        this.defectReportingService = defectReportingService;
    }

    @ActivityMethod
    public String reportDefectViaTemporal(String title, String body, String project, String severity) {
        ReportDefectCmd cmd = new ReportDefectCmd(title, body, project, severity);
        defectReportingService.reportDefect(cmd);
        return "Reported: " + title;
    }
}

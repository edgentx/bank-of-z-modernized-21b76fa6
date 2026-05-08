package com.example.domain.validation;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.activity.ActivityInterface;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity implementation.
 * This bridges the Temporal workflow execution with the Spring Boot service logic.
 */
@ActivityInterface
public interface ReportDefectActivity {
    void reportDefect(String title, String body);
}

/**
 * Implementation of the Activity.
 * Delegates directly to the DefectReporterService.
 */
@Component
@ActivityImpl(taskQueue = "VFORCE360_TASK_QUEUE")
public class ReportDefectActivityImpl implements ReportDefectActivity {

    private final DefectReporterService reporter;

    public ReportDefectActivityImpl(DefectReporterService reporter) {
        this.reporter = reporter;
    }

    @Override
    public void reportDefect(String title, String body) {
        reporter.reportDefect(title, body);
    }
}

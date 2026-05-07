package com.example.application;

import com.example.domain.verification.model.ReportDefectCommand;
import com.example.domain.verification.service.VerificationService;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity Definition for Defect Reporting.
 * Story S-FB-1: Orchestrates the creation of an issue and Slack notification.
 */
@ActivityInterface
public interface DefectReportingActivity {

    @ActivityMethod
    void reportDefect(ReportDefectCommand command);

    /**
     * Implementation of the Activity.
     * Delegates to the VerificationService domain logic.
     */
    @Component
    @ActivityImpl(taskQueue = "DEFECT_REPORTING_TASK_QUEUE")
    class DefectReportingActivityImpl implements DefectReportingActivity {

        private static final Logger log = LoggerFactory.getLogger(DefectReportingActivityImpl.class);

        private final VerificationService verificationService;

        public DefectReportingActivityImpl(VerificationService verificationService) {
            this.verificationService = verificationService;
        }

        @Override
        public void reportDefect(ReportDefectCommand command) {
            log.info("Executing reportDefect activity for: {}", command.defectId());
            // The VerificationService is expected to be a Spring-managed bean
            // constructed with the real ports (Adapters).
            verificationService.reportDefect(command);
        }
    }
}

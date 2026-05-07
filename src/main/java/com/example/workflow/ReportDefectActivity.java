package com.example.workflow;

import com.example.domain.verification.service.VerificationService;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity Definition for reporting a defect.
 * This interfaces with the VerificationService domain logic.
 */
@ActivityInterface
public interface ReportDefectActivity {

    @ActivityMethod
    void executeReport(String validationId, String description, String reporter, String severity);

    /**
     * Implementation of the Temporal Activity.
     * Wraps the Spring Bean VerificationService.
     */
    @Component
    @ActivityImpl(taskQueue = "DEFECT_TASK_QUEUE")
    class ReportDefectActivityImpl implements ReportDefectActivity {

        private final VerificationService verificationService;

        public ReportDefectActivityImpl(VerificationService verificationService) {
            this.verificationService = verificationService;
        }

        @Override
        public void executeReport(String validationId, String description, String reporter, String severity) {
            verificationService.reportDefectViaTemporal(validationId, description, reporter, severity);
        }
    }
}

package com.example.workers;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.service.ValidationService;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity interface definition for Defect Reporting.
 */
@ActivityInterface
public interface ReportDefectActivity {

    @ActivityMethod
    void reportDefect(ReportDefectCmd cmd);

    /**
     * Implementation wrapper that connects the Temporal Activity interface to the Domain Service.
     * Registered as a Bean to be picked up by the Temporal Worker.
     */
    @Component
    class ReportDefectActivityImpl implements ReportDefectActivity {
        private final ValidationService validationService;

        public ReportDefectActivityImpl(ValidationService validationService) {
            this.validationService = validationService;
        }

        @Override
        public void reportDefect(ReportDefectCmd cmd) {
            validationService.reportDefect(cmd);
        }
    }
}

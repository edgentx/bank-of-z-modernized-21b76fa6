package com.example.infrastructure;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.port.DefectRepository;
import com.example.domain.validation.model.VerifySlackLinkCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.port.ValidationRepository;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Temporal Activities Implementation.
 * This class contains the actual logic executed by the Temporal workflow.
 * It interacts with the domain aggregates via repositories.
 */
@Service
public class TemporalActivitiesImpl {

    private final DefectRepository defectRepository;
    private final ValidationRepository validationRepository;

    public TemporalActivitiesImpl(DefectRepository defectRepository, ValidationRepository validationRepository) {
        this.defectRepository = defectRepository;
        this.validationRepository = validationRepository;
    }

    /**
     * Reports a defect, generating the GitHub URL.
     * Corresponds to _report_defect activity.
     */
    public void reportDefect(String defectId, String title, String description) {
        DefectAggregate defect = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, title, description);
        
        defect.execute(cmd);
        defectRepository.save(defect);
    }

    /**
     * Validates that the Slack body contains the expected GitHub URL.
     * Corresponds to the validation step in the workflow.
     */
    public boolean validateSlackBody(String validationId, String slackBody, String expectedUrl) {
        ValidationAggregate validation = new ValidationAggregate(validationId);
        VerifySlackLinkCmd cmd = new VerifySlackLinkCmd(validationId, slackBody, expectedUrl);
        
        List<io.temporal.workflow.Workflow> events = (List<io.temporal.workflow.Workflow>) validation.execute(cmd);
        validationRepository.save(validation);
        
        return validation.isLinkVerified();
    }

    /**
     * Interface definition for Temporal to discover activities.
     */
    @ActivityInterface
    public interface DefectActivities {
        @ActivityMethod
        void reportDefect(String defectId, String title, String description);

        @ActivityMethod
        boolean validateSlackBody(String validationId, String slackBody, String expectedUrl);
    }
}

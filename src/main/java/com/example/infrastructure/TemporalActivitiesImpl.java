package com.example.infrastructure;

import com.example.domain.shared.SlackMessageValidator;
import com.example.ports.SlackNotifier;
import com.example.domain.defect.repository.DefectRepository;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ActivityInterface
public interface TemporalActivities {
    @ActivityMethod
    String reportDefectActivity(String defectJson);
}

public class TemporalActivitiesImpl implements TemporalActivities {
    private static final Logger log = LoggerFactory.getLogger(TemporalActivitiesImpl.class);
    private final SlackNotifier slackNotifier;
    private final DefectRepository defectRepository;

    public TemporalActivitiesImpl(SlackNotifier slackNotifier, DefectRepository defectRepository) {
        this.slackNotifier = slackNotifier;
        this.defectRepository = defectRepository;
    }

    @Override
    public String reportDefectActivity(String defectJson) {
        // Implementation for story S-FB-1
        // The defect is assumed to be parsed/hydrated here for the E2E flow
        // In a real scenario, we would parse JSON, but we assume the Aggregate
        // is populated for the verification step.

        // For the purpose of the test, we will simulate the flow where
        // we load an aggregate, verify its content, and notify Slack.
        // Note: In a real Temporal worker, this would involve deserialization.
        
        // Mocking the 'process' of validating URL presence before sending
        // (VW-454 validation)
        
        // This is a placeholder for the actual logic that would be compiled.
        // The TDD test will drive the implementation of the workflow service
        // which utilizes this activity.
        
        log.info("Executing reportDefectActivity via Temporal");
        return "ActivityExecuted";
    }
}

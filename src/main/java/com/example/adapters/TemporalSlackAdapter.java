package com.example.adapters;

import com.example.domain.shared.ValidationResult;
import com.example.ports.DefectReporterPort;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Real implementation of DefectReporterPort using Temporal and Slack.
 * This acts as an Adapter for the Temporal workflow engine.
 */
public class TemporalSlackAdapter implements DefectReporterPort {

    private static final Logger log = LoggerFactory.getLogger(TemporalSlackAdapter.class);
    private final SlackActivities activities;

    // Constructor injection of the Temporal Activity stub
    public TemporalSlackAdapter(SlackActivities activities) {
        this.activities = activities;
    }

    @Override
    public void reportDefect(ValidationResult result, String githubUrl) {
        log.info("Reporting defect via Temporal: result={}, url={}", result.getMessage(), githubUrl);
        // Invoke the Temporal Activity. This executes the async logic.
        activities.sendSlackNotification(result.getMessage(), githubUrl);
    }

    /**
     * Interface defining Temporal Activities.
     * Temporal uses interfaces to define execution units.
     */
    @ActivityInterface
    public interface SlackActivities {
        @ActivityMethod
        void sendSlackNotification(String message, String githubUrl);
    }

    /**
     * Implementation of the Activity.
     * This is where the actual external side-effect (Slack API call) would occur.
     */
    public static class SlackActivitiesImpl implements SlackActivities {
        @Override
        public void sendSlackNotification(String message, String githubUrl) {
            // This is the actual logic block being tested.
            // In a real scenario, this would use WebClient to POST to Slack.
            String body = "Defect reported: " + message;
            if (githubUrl != null) {
                body += "\nGitHub issue: " + githubUrl;
            }
            System.out.println("[SLACK OUTBOUND] " + body); // Simulated send
        }
    }
}
package com.example.workers;

import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Temporal Activity Interface for Defect Reporting.
 * Wraps external port calls to ensure they are executed within the activity context.
 */
@ActivityInterface
public interface ReportDefectActivity {

    @ActivityMethod
    String reportDefect(String summary, String description, String slackChannel);

    // Expose ports for the implementation (injected via Workflow)
    // In a real setup, these might be passed directly to the activity method
    // or the Activity implementation class would hold references to the Port beans.
}

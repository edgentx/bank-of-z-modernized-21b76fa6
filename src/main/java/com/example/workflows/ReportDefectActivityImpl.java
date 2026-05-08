package com.example.workflows;

import com.example.ports.SlackPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Temporal Activity interface.
 * Delegates the actual Slack communication to the SlackAdapter via the SlackPort.
 */
@Component
public class ReportDefectActivityImpl implements ReportDefectActivity {

    private final SlackPort slackAdapter;

    @Autowired
    public ReportDefectActivityImpl(SlackPort slackAdapter) {
        this.slackAdapter = slackAdapter;
    }

    @Override
    public String reportDefectToSlack(String message, String githubUrl) {
        // Delegate to the port implementation
        return slackAdapter.postDefectNotification(message, githubUrl);
    }
}

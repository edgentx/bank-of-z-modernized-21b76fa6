package com.example.application;

import com.example.adapters.GitHubHttpAdapter;
import com.example.adapters.SlackHttpAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Defect Reporting Activity.
 * This class is invoked by the Temporal Workflow and delegates to external ports (Adapters).
 */
@Component
@ActivityImpl(taskQueues = "DEFECT_REPORTING_TASK_QUEUE")
public class DefectReportingActivityImpl implements DefectReportingActivity {

    private static final Logger log = LoggerFactory.getLogger(DefectReportingActivityImpl.class);

    private final SlackPort slackPort;
    private final GitHubPort gitHubPort;

    /**
     * Constructor injection used to satisfy Adapter/Port pattern requirements.
     * Spring will automatically inject the specific adapters (e.g., SlackHttpAdapter).
     */
    public DefectReportingActivityImpl(SlackPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    @Override
    public boolean postToSlack(String channel, String body) {
        log.info("Activity: postToSlack channel={}, body='{}'", channel, body);
        return slackPort.sendMessage(channel, body);
    }

    @Override
    public String createGitHubIssue(String title, String body) {
        log.info("Activity: createGitHubIssue title={}", title);
        return gitHubPort.createIssue(title, body);
    }
}